package com.github.nagyesta.abortmission.booster.cucumber;

import com.github.nagyesta.abortmission.booster.cucumber.matcher.ScenarioNameMatcher;
import com.github.nagyesta.abortmission.booster.cucumber.matcher.ScenarioUriMatcher;
import com.github.nagyesta.abortmission.core.AbortMissionCommandOps;
import com.github.nagyesta.abortmission.core.AbortMissionGlobalConfiguration;
import com.github.nagyesta.abortmission.core.MissionControl;
import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;
import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;
import com.github.nagyesta.abortmission.core.outline.MissionOutline;
import com.github.nagyesta.abortmission.core.telemetry.watch.StageTimeStopwatch;
import io.cucumber.java.Scenario;
import io.cucumber.plugin.event.Result;
import io.cucumber.plugin.event.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;

/**
 * Provides a close to complete Cucumber Hook implementation we can readily reuse when integrating Abort-Mission.
 */
public abstract class LaunchAbortHook {

    private static final Logger LOGGER = LoggerFactory.getLogger(LaunchAbortHook.class);
    private final CucumberLaunchSequenceTemplate launchSequenceTemplate =
            new CucumberLaunchSequenceTemplate(this::doAbort, this::findEvaluators);
    private final MissionOutline missionOutline = new MissionOutline() {
        @Override
        protected Map<String, Consumer<AbortMissionCommandOps>> defineOutline() {
            return LaunchAbortHook.this.defineOutline();
        }
    };

    private final Map<String, StageTimeStopwatch> stopwatchMap = new HashMap<>();

    /**
     * Returns a {@link MissionHealthCheckMatcher} matching any scenario URI.
     *
     * @return matcher
     */
    public static MissionHealthCheckMatcher anyScenarioMatcher() {
        return new ScenarioUriMatcher(".*");
    }

    /**
     * Returns a {@link MissionHealthCheckMatcher} matching scenario URIs.
     *
     * @param regex The regular expression matching the URI of a feature file.
     * @return matcher
     */
    public static MissionHealthCheckMatcher scenarioUriMatcher(final String regex) {
        return new ScenarioUriMatcher(regex);
    }

    /**
     * Returns a {@link MissionHealthCheckMatcher} matching scenario names.
     *
     * @param regex The regular expression matching the name of a scenario as defined in the feature file.
     * @return matcher
     */
    public static MissionHealthCheckMatcher scenarioNameMatcher(final String regex) {
        return new ScenarioNameMatcher(regex);
    }

    /**
     * Implement this the same way you would for {@link MissionOutline}.
     *
     * @return The map defining the matchers and evaluators.
     */
    protected abstract Map<String, Consumer<AbortMissionCommandOps>> defineOutline();

    /**
     * Implement this method, annotate with the {@link io.cucumber.java.Before} annotation,
     * then make sure to call {@link #doBeforeScenario(Scenario)}.
     *
     * @param scenario The scenario we are running.
     */
    public abstract void beforeScenario(Scenario scenario);

    /**
     * Implement this method, annotate with the {@link io.cucumber.java.After} annotation,
     * then make sure to call {@link #doAfterScenario(Scenario)}.
     *
     * @param scenario The scenario we are running.
     */
    public abstract void afterScenario(Scenario scenario);

    /**
     * Implement this method based on the test framework you are using.
     * It should be throwing one of the following exceptions:
     * <ul>
     *     <li>org.junit.AssumptionViolatedException</li>
     *     <li>org.junit.internal.AssumptionViolatedException</li>
     *     <li>org.opentest4j.TestAbortedException</li>
     *     <li>org.testng.SkipException"</li>
     * </ul>
     */
    protected abstract void doAbort();

    /**
     * This is the method that needs to be called from {@link #beforeScenario(Scenario)}.
     *
     * @param scenario The scenario.
     */
    protected void doBeforeScenario(final Scenario scenario) {
        missionOutline.initialBriefing();
        overrideGlobalConfig(MissionControl.globalConfiguration());
        storeOptionalStopwatch(scenario, launchSequenceTemplate.launchImminent(scenario));
    }

    /**
     * This is the method that needs to be called from {@link #afterScenario(Scenario)}.
     *
     * @param scenario The scenario.
     */
    protected void doAfterScenario(final Scenario scenario) {
        if (scenario.isFailed()) {
            launchSequenceTemplate.launchFailure(scenario, getThrowable(scenario), getOptionalStopwatch(scenario));
        } else {
            launchSequenceTemplate.launchSuccess(scenario, getOptionalStopwatch(scenario));
        }
    }

    /**
     * This method should be overridden if you want to provide custom global configuration.
     *
     * @param config The global configuration.
     */
    protected void overrideGlobalConfig(final AbortMissionGlobalConfiguration config) {
        //noop
    }

    @SuppressWarnings({"unchecked", "java:S3011"})
    private Optional<Throwable> getThrowable(final Scenario scenario) {
        try {
            final var delegate = scenario.getClass().getDeclaredField("delegate");
            delegate.setAccessible(true);
            final var testCaseState = delegate.get(scenario);
            final var stepResults = testCaseState.getClass().getDeclaredField("stepResults");
            stepResults.setAccessible(true);
            final var results = (List<Result>) stepResults.get(testCaseState);
            return Optional.ofNullable(results)
                    .flatMap(r -> r.stream()
                            .filter(res -> res.getStatus() == Status.FAILED)
                            .map(Result::getError)
                            .findFirst());
        } catch (final Exception e) {
            LOGGER.error("Couldn't find throwable, substituting with an Exception for reporting.\n"
                    + "Failure suppression will not be accurate. Caused by: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private void storeOptionalStopwatch(
            final Scenario scenario,
            final Optional<StageTimeStopwatch> stopwatch) {
        if (stopwatch.isPresent()) {
            stopwatchMap.put(scenario.getId(), stopwatch.get());
        } else {
            stopwatchMap.remove(scenario.getId());
        }
    }

    private Optional<StageTimeStopwatch> getOptionalStopwatch(final Scenario scenario) {
        return Optional.ofNullable(stopwatchMap.get(scenario.getId()));
    }

    private Set<MissionHealthCheckEvaluator> findEvaluators(final Scenario scenario) {
        final var context = scenario.getSourceTagNames().stream()
                .filter(tag -> tag.startsWith("@AbortMission_Context_"))
                .map(tag -> tag.replaceFirst("^@AbortMission_Context_", ""))
                .findFirst();
        final var commandOps =
                context.map(MissionControl::commandOps).orElse(MissionControl.commandOps());
        return Objects.requireNonNull(commandOps, "Mission context is not found: " + context.orElse("'- DEFAULT - '"))
                .matchingEvaluators(scenario);
    }
}
