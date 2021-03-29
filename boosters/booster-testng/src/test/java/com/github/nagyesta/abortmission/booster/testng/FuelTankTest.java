package com.github.nagyesta.abortmission.booster.testng;

import com.github.nagyesta.abortmission.booster.testng.listener.AbortMissionListener;
import com.github.nagyesta.abortmission.core.MissionControl;
import org.testng.ITestContext;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.annotations.Test;

import java.util.Arrays;

import static com.github.nagyesta.abortmission.testkit.vanilla.FuelTankTestAssets.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class FuelTankTest {

    @Test(groups = "integration")
    @SuppressWarnings("checkstyle:MagicNumber")
    public void testAssumption() {
        final TestNG engine = new TestNG();
        engine.setOutputDirectory(System.getProperty("java.io.tmpdir") + "/abort-mission/" + this.getClass().getSimpleName());
        engine.setTestClasses(new Class[] {FuelTankTestContext.class});
        engine.setListenerClasses(Arrays.asList(AbortMissionListener.class, ValidatingTestListener.class));
        engine.run();
        assertTrue(engine.hasFailure());
        MissionControl.matchingHealthChecks(CONTEXT_NAME, FuelTankTestContext.class)
                .forEach(evaluator -> {
                    assertEquals(FUEL_TANK_NOMINAL_STATS_PER_CLASS.getReadOnlyCountdown().getSnapshot(),
                            evaluator.getStats().getReadOnlyCountdown().getSnapshot());
                    assertEquals(FUEL_TANK_NOMINAL_STATS_PER_CLASS.getReadOnlyMission().getSnapshot(),
                            evaluator.getStats().getReadOnlyMission().getSnapshot());
                });
    }

    public static class ValidatingTestListener extends TestListenerAdapter {
        @Override
        public void onFinish(final ITestContext testContext) {
            super.onFinish(testContext);
            assertEquals(this.getPassedTests().size(), SUCCESSFUL_CASES);
            assertEquals(this.getSkippedTests().size(), ABORTED_CASES);
            assertEquals(this.getFailedTests().size(), FAILED_CASES);
        }
    }

}
