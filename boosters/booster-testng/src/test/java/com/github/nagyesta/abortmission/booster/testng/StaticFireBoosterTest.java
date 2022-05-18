package com.github.nagyesta.abortmission.booster.testng;

import com.github.nagyesta.abortmission.booster.testng.listener.AbortMissionListener;
import com.github.nagyesta.abortmission.core.MissionControl;
import org.testng.ITestContext;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.annotations.Test;
import org.testng.xml.XmlSuite;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.nagyesta.abortmission.testkit.spring.StaticFireTestAssets.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertFalse;

public class StaticFireBoosterTest {

    private static final AtomicInteger FAILED = new AtomicInteger(0);
    private static final AtomicInteger SKIPPED = new AtomicInteger(0);
    private static final AtomicInteger PASSED = new AtomicInteger(0);
    private static final AtomicInteger PARALLEL_FAILED = new AtomicInteger(0);
    private static final AtomicInteger PARALLEL_SKIPPED = new AtomicInteger(0);
    private static final AtomicInteger PARALLEL_PASSED = new AtomicInteger(0);
    private static final int CASES_SKIPPED_DUE_TO_CONFIG_ERROR = 501;
    private static final int FAILED_CASES_CONFIG_ERROR_ONLY = 1;

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
                .forEach(evaluator -> {
                    assertEquals(SIDE_BOOSTER_NOMINAL_STATS_PER_CLASS.getReadOnlyCountdown().getSnapshot(),
                            evaluator.getStats().getReadOnlyCountdown().getSnapshot());
                    assertEquals(SIDE_BOOSTER_NOMINAL_STATS_PER_CLASS.getReadOnlyMission().getSnapshot(),
                            evaluator.getStats().getReadOnlyMission().getSnapshot());
                });
        MissionControl.matchingHealthChecks(STATIC_FIRE, StaticFireTestCenterCoreOnly.class.getDeclaredMethod("testIsOnFire"))
                .forEach(evaluator -> {
                    assertEquals(CENTER_CORE_NOMINAL_STATS.getReadOnlyCountdown().getSnapshot(),
                            evaluator.getStats().getReadOnlyCountdown().getSnapshot());
                    assertEquals(CENTER_CORE_NOMINAL_STATS.getReadOnlyMission().getSnapshot(),
                            evaluator.getStats().getReadOnlyMission().getSnapshot());
                });
    }

    @Test(groups = "integration")
    @SuppressWarnings("checkstyle:MagicNumber")
    public void testParallelAssumption() {
        final TestNG engine = new TestNG();
        engine.setOutputDirectory(System.getProperty("java.io.tmpdir") + "/abort-mission/" + this.getClass().getSimpleName());
        engine.setTestClasses(new Class[] {ParallelStaticFireTestWithSideBoosters.class});
        engine.setListenerClasses(Arrays.asList(AbortMissionListener.class, ParallelValidatingTestListener.class));
        engine.setParallel(XmlSuite.ParallelMode.METHODS);
        engine.setThreadCount(4);
        engine.run();
        assertFalse(engine.hasFailure());
        assertEquals(PARALLEL_PASSED.get(), SUCCESSFUL_PARALLEL_CASES);
        assertEquals(PARALLEL_FAILED.get(), 0);
        assertEquals(PARALLEL_SKIPPED.get(), 0);
        MissionControl.matchingHealthChecks(PARALLEL, ParallelStaticFireTestWithSideBoosters.class)
                .forEach(evaluator -> {
                    assertEquals(PARALLEL_NOMINAL_STATS_PER_CLASS.getReadOnlyCountdown().getSnapshot(),
                            evaluator.getStats().getReadOnlyCountdown().getSnapshot());
                    assertEquals(PARALLEL_NOMINAL_STATS_PER_CLASS.getReadOnlyMission().getSnapshot(),
                            evaluator.getStats().getReadOnlyMission().getSnapshot());
                });
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

    public static class ParallelValidatingTestListener extends TestListenerAdapter {
        @Override
        public void onFinish(final ITestContext testContext) {
            super.onFinish(testContext);
            PARALLEL_FAILED.addAndGet(this.getFailedTests().size());
            PARALLEL_FAILED.addAndGet(this.getConfigurationFailures().size());
            PARALLEL_SKIPPED.addAndGet(this.getSkippedTests().size());
            PARALLEL_PASSED.addAndGet(this.getPassedTests().size());
        }
    }

}
