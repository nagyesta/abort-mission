package com.github.nagyesta.abortmission.booster.cucumber;

import com.github.nagyesta.abortmission.core.AbstractMissionLaunchSequenceTemplate;
import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;
import com.github.nagyesta.abortmission.core.telemetry.StageResult;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import com.github.nagyesta.abortmission.core.telemetry.watch.StageTimeStopwatch;
import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Provides support for the whole lifecycle of the mission in a reasonably flexible format to allow easy test
 * framework integration.
 */
public class CucumberLaunchSequenceTemplate extends AbstractMissionLaunchSequenceTemplate {

    private static final Logger LOGGER = LoggerFactory.getLogger(CucumberLaunchSequenceTemplate.class);
    private final Function<Scenario, Set<MissionHealthCheckEvaluator>> scenarioBasedEvaluatorLookup;

    /**
     * Constructor defining how the template can look up the evaluators or abort the mission.
     *
     * @param abortSequence                The {@link Runnable} which aborts the tests (usually by throwing an exception)
     * @param scenarioBasedEvaluatorLookup The {@link Function} that will be used for evaluator lookup.
     */
    public CucumberLaunchSequenceTemplate(
            final Runnable abortSequence,
            final Function<Scenario, Set<MissionHealthCheckEvaluator>> scenarioBasedEvaluatorLookup) {
        super(abortSequence);
        this.scenarioBasedEvaluatorLookup = Objects.requireNonNull(scenarioBasedEvaluatorLookup, "Scenario evaluator cannot be null.");
    }

    /**
     * Marks completion of the test instance preparation.
     *
     * @param scenario The test Scenario, which is ready for execution.
     * @return A stageTimeStopwatch started to measure execution times (won't be present if reporting already happened).
     */
    public Optional<StageTimeStopwatch> launchImminent(final Scenario scenario) {
        LOGGER.debug("Preparing mission for scenario from URI: {} named: {}", scenario.getUri(), scenario.getName());
        final var evaluators = scenarioBasedEvaluatorLookup.apply(scenario);
        final var countdownStopwatch = new StageTimeStopwatch(scenario.getUri().toString(), StageTimeMeasurement.CLASS_ONLY)
                .overrideDisplayName(scenario.getName() + " (" + scenario.getUri() + ":" + scenario.getLine() + ")");
        evaluators.forEach(e -> e.countdownLogger().logAndIncrement(countdownStopwatch.stop().apply(StageResult.SUCCESS)));
        return evaluateLaunchAbort(evaluators,
                new StageTimeStopwatch(scenario.getUri().toString(), scenario.getName())
                        .overrideDisplayName(scenario.getName() + " (" + scenario.getUri() + ":" + scenario.getLine() + ")"),
                () -> scenario.getSourceTagNames().stream().anyMatch("AbortMission_SuppressAbort"::equalsIgnoreCase)
        );
    }

    /**
     * Indicates that a failure happened during a test run.
     *
     * @param scenario  The test Scenario we executed.
     * @param rootCause The root cause of the failure.
     * @param stopwatch Captures when the launch started.
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public void launchFailure(
            final Scenario scenario,
            final Optional<Throwable> rootCause,
            final Optional<StageTimeStopwatch> stopwatch) {
        LOGGER.debug("Mission failed for scenario from URI: {} named: {}", scenario.getUri(), scenario.getName());
        missionFailureDetected(scenarioBasedEvaluatorLookup.apply(scenario), stopwatch,
                Optional.of(rootCause.orElse(new Exception())), findSuppressedExceptions(scenario)
        );
    }

    /**
     * Wraps up the mission by logging a successful run.
     *
     * @param scenario  The test Scenario, which was executed.
     * @param stopwatch Captures when the launch started.
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public void launchSuccess(
            final Scenario scenario,
            final Optional<StageTimeStopwatch> stopwatch) {
        LOGGER.debug("Mission successful for scenario from URI: {} named: {}", scenario.getUri(), scenario.getName());
        missionCompletedSuccessfully(scenarioBasedEvaluatorLookup.apply(scenario), stopwatch);
    }

    private Set<Class<? extends Exception>> findSuppressedExceptions(final Scenario scenario) {
        final Set<Class<? extends Exception>> suppressedExceptions = new HashSet<>();
        scenario.getSourceTagNames().stream()
                .filter(tag -> tag.startsWith("@AbortMission_SuppressFailure_"))
                .map(tag -> tag.replaceFirst("^@AbortMission_SuppressFailure_", ""))
                .forEach(tag -> {
                    try {
                        final var aClass = Class.forName(tag);
                        if (Exception.class.isAssignableFrom(aClass)) {
                            suppressedExceptions.add(aClass.asSubclass(Exception.class));
                        }
                    } catch (final Exception exception) {
                        LOGGER.warn("Failed to process tag: @AbortMission_SuppressFailure_{}", tag, exception);
                    }
                });
        return suppressedExceptions;
    }
}
