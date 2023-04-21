package com.github.nagyesta.abortmission.booster.cucumber.staticfire;

import com.github.nagyesta.abortmission.booster.cucumber.LaunchAbortHook;
import com.github.nagyesta.abortmission.booster.cucumber.matcher.TagDependencyNameExtractor;
import com.github.nagyesta.abortmission.core.AbortMissionCommandOps;
import com.github.nagyesta.abortmission.core.AbortMissionGlobalConfiguration;
import com.github.nagyesta.abortmission.testkit.spring.StaticFireTestAssets;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.junit.AssumptionViolatedException;

import java.util.Map;
import java.util.function.Consumer;

public class AbortMissionHook extends LaunchAbortHook {

    @Override
    protected Map<String, Consumer<AbortMissionCommandOps>> defineOutline() {
        return StaticFireTestAssets.getMissionPlan(new TagDependencyNameExtractor());
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
