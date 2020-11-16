package com.github.nagyesta.abortmission.booster.testng;

import com.github.nagyesta.abortmission.booster.testng.listener.AbortMissionListener;
import com.github.nagyesta.abortmission.core.MissionControl;
import org.testng.ITestContext;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.nagyesta.abortmission.testkit.spring.StaticFireTestAssets.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class StaticFireBoosterTest {

    private static final AtomicInteger FAILED = new AtomicInteger(0);
    private static final AtomicInteger SKIPPED = new AtomicInteger(0);
    private static final AtomicInteger PASSED = new AtomicInteger(0);
    private static final int CASES_SKIPPED_DUE_TO_CONFIG_ERROR = 501;
    private static final int FAILED_CASES_CONFIG_ERROR_ONLY = 1;

//    private static final ReadOnlyMissionStatistics SIDE_BOOSTER_NOMINAL_STATS_CONFIG_FAILURE =
//            new MissionStatisticsCollector(CASES_SKIPPED_DUE_TO_CONFIG_ERROR, 0, 499, 0, 0, 0);
//    private static final ReadOnlyMissionStatistics CENTER_CORE_NOMINAL_STATS_CONFIG_FAILURE =
//            new MissionStatisticsCollector(0, 1, 0, 1, 0, 0);

    @Test(groups = "integration")
    @SuppressWarnings("checkstyle:MagicNumber")
    public void testAssumption() throws NoSuchMethodException {
        final TestNG engine = new TestNG();
        engine.setOutputDirectory(System.getProperty("java.io.tmpdir") + "/abort-mission/" + this.getClass().getSimpleName());
        engine.setTestClasses(new Class[] {StaticFireTestWithSideBoosters.class, StaticFireTestCenterCoreOnly.class});
        engine.setListenerClasses(Arrays.asList(AbortMissionListener.class, ValidatingTestListener.class));
        engine.run();
        assertTrue(engine.hasFailure());
        assertEquals(PASSED.get(), SUCCESSFUL_CASES);
        assertEquals(FAILED.get(), FAILED_CASES_CONFIG_ERROR_ONLY);
        assertEquals(SKIPPED.get(), CASES_SKIPPED_DUE_TO_CONFIG_ERROR);
        MissionControl.matchingHealthChecks(STATIC_FIRE, StaticFireTestWithSideBoosters.class)
                .forEach(evaluator -> assertEquals(evaluator.getStats(), SIDE_BOOSTER_NOMINAL_STATS_PER_CLASS));
        MissionControl.matchingHealthChecks(STATIC_FIRE, StaticFireTestCenterCoreOnly.class.getDeclaredMethod("testIsOnFire"))
                .forEach(evaluator -> assertEquals(evaluator.getStats(), CENTER_CORE_NOMINAL_STATS));
    }


    public static class ValidatingTestListener extends TestListenerAdapter {
        @Override
        public void onFinish(final ITestContext testContext) {
            super.onFinish(testContext);
            FAILED.addAndGet(this.getFailedTests().size());
            FAILED.addAndGet(this.getConfigurationFailures().size());
            SKIPPED.addAndGet(this.getSkippedTests().size());
            PASSED.addAndGet(this.getPassedTests().size());
        }
    }

}
