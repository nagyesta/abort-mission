package com.github.nagyesta.abortmission.booster.testng;

import com.github.nagyesta.abortmission.booster.testng.listener.AbortMissionListener;
import com.github.nagyesta.abortmission.core.MissionControl;
import org.testng.ITestContext;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.nagyesta.abortmission.testkit.LaunchEvaluationUtil.*;
import static com.github.nagyesta.abortmission.testkit.vanilla.ParachuteDropTestAssets.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ParachuteDropTest {

    private static final List<String> EXPECTED_DISPLAY_NAMES_COUNTDOWN = List.of("ParachuteTestContext");
    private static final List<String> EXPECTED_DISPLAY_NAMES = parachuteDropTestInputProvider()
            .mapToObj(i -> "testParachuteShouldOpenWhenCalled " + i)
            .collect(Collectors.toList());

    @Test(groups = "integration")
    @SuppressWarnings("checkstyle:MagicNumber")
    public void testAssumption() {
        final TestNG engine = new TestNG();
        engine.setOutputDirectory(System.getProperty("java.io.tmpdir") + "/abort-mission/" + this.getClass().getSimpleName());
        engine.setTestClasses(new Class[] {ParachuteTestContext.class});
        engine.setListenerClasses(Arrays.asList(AbortMissionListener.class, ValidatingTestListener.class));
        engine.run();
        assertTrue(engine.hasFailure());
        MissionControl.matchingHealthChecks(ParachuteTestContext.class)
                .forEach(evaluator -> {
                    assertEquals(PARACHUTE_NOMINAL_STATS_PER_CLASS.getReadOnlyCountdown().getSnapshot(),
                            evaluator.getStats().getReadOnlyCountdown().getSnapshot());
                    assertEquals(PARACHUTE_NOMINAL_STATS_PER_CLASS.getReadOnlyMission().getSnapshot(),
                            evaluator.getStats().getReadOnlyMission().getSnapshot());
                    //check display names
                    assertEquals(findCountdownDisplayNamesForMeasurementsOf(evaluator), EXPECTED_DISPLAY_NAMES_COUNTDOWN);
                    assertEquals(findMissionDisplayNamesForMeasurementsOf(evaluator), EXPECTED_DISPLAY_NAMES);
                    //check exception details
                    assertEquals(findExceptionsForMissionFailuresOf(evaluator), List.of(AssertionError.class.getName()));
                    assertEquals(findThrowableMessagesForMissionFailuresOf(evaluator),
                            List.of("Parachutes should open. expected [true] but found [false]"));
                    forEachNonFilteredStackTraceElementOfMissionFailures(evaluator,
                            e -> assertTrue(e.startsWith("com.github.nagyesta"), "Unexpected stack trace: " + e));
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
