package com.github.nagyesta.abortmission.booster.cucumber.weather;

import com.github.nagyesta.abortmission.booster.cucumber.LaunchAbortHook;
import com.github.nagyesta.abortmission.core.AbortMissionCommandOps;
import com.github.nagyesta.abortmission.core.AbortMissionGlobalConfiguration;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.junit.AssumptionViolatedException;

import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

import static com.github.nagyesta.abortmission.core.MissionControl.percentageBasedEvaluator;
import static com.github.nagyesta.abortmission.testkit.vanilla.WeatherTestAssets.CONTEXT_NAME;

public class AbortMissionHook extends LaunchAbortHook {

    @Override
    protected Map<String, Consumer<AbortMissionCommandOps>> defineOutline() {
        final var weatherEvaluator = percentageBasedEvaluator(scenarioNameMatcher("^Weather.+"))
                .abortThreshold(1)
                .build();
        final var launchEvaluator = percentageBasedEvaluator(scenarioNameMatcher("^Launch.+"))
                .dependsOn(weatherEvaluator)
                .abortThreshold(1)
                .build();
        return Collections.singletonMap(CONTEXT_NAME, ops -> {
            ops.registerHealthCheck(weatherEvaluator);
            ops.registerHealthCheck(launchEvaluator);
        });
    }

    @Override
    protected void overrideGlobalConfig(final AbortMissionGlobalConfiguration config) {
        config.setStackTraceFilter(stackTraceElement -> stackTraceElement.getClassName().startsWith("com.github."));
    }

    @Before
    @Override
    public void beforeScenario(final Scenario scenario) {
        doBeforeScenario(scenario);
    }

    @After
    @Override
    public void afterScenario(final Scenario scenario) {
        doAfterScenario(scenario);
    }

    @Override
    protected void doAbort() {
        throw new AssumptionViolatedException("Aborting as the launch failure threshold is reached.");
    }
}
