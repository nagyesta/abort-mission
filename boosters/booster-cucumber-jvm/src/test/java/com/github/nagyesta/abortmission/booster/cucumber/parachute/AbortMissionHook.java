package com.github.nagyesta.abortmission.booster.cucumber.parachute;

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
import static com.github.nagyesta.abortmission.core.outline.MissionOutline.SHARED_CONTEXT;

public class AbortMissionHook extends LaunchAbortHook {

    @Override
    protected Map<String, Consumer<AbortMissionCommandOps>> defineOutline() {
        return Collections.singletonMap(SHARED_CONTEXT, ops -> {
            ops.registerHealthCheck(percentageBasedEvaluator(scenarioUriMatcher("classpath:([a-z]+/)+ParachuteDrop\\.feature"))
                    .abortThreshold(1)
                    .build());
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
