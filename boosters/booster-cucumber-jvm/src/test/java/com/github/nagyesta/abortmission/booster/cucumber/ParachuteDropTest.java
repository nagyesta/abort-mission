package com.github.nagyesta.abortmission.booster.cucumber;

import com.github.nagyesta.abortmission.booster.cucumber.parachute.ParachuteTestContext;
import com.github.nagyesta.abortmission.core.MissionControl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineTestKit;

import java.util.List;
import java.util.stream.IntStream;

import static com.github.nagyesta.abortmission.testkit.LaunchEvaluationUtil.*;
import static com.github.nagyesta.abortmission.testkit.vanilla.ParachuteDropTestAssets.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

class ParachuteDropTest {

    private static final int FIRST_LINE_OF_EXAMPLES = 10;
    private static final int LAST_LINE_OF_EXAMPLES = 19;
    private static final String RESOURCE_NAME =
            "classpath:com/github/nagyesta/abortmission/booster/cucumber/parachute/ParachuteDrop.feature";
    private static final List<String> EXPECTED_DISPLAY_NAMES = IntStream.rangeClosed(FIRST_LINE_OF_EXAMPLES, LAST_LINE_OF_EXAMPLES)
            .mapToObj(i -> "Parachute_1 Parachutes should open (" + RESOURCE_NAME + ":" + i + ")")
            .toList();

    @Test
    @Tag("integration")
    @SuppressWarnings("checkstyle:MagicNumber")
    void testAssumption() {
        EngineTestKit
                .engine("junit-vintage")
                .selectors(selectClass(ParachuteTestContext.class))
                .execute()
                .testEvents()
                .assertStatistics(stats -> stats
                        .skipped(DISABLED_CASES)
                        .started(TOTAL_CASES)
                        .succeeded(SUCCESSFUL_CASES)
                        .aborted(ABORTED_CASES)
                        .failed(FAILED_CASES));
        MissionControl.commandOps().allEvaluators()
                .forEach(evaluator -> {
                    assertEquals(PARACHUTE_NOMINAL_STATS.getReadOnlyMission().getSnapshot(),
                            evaluator.getStats().getReadOnlyMission().getSnapshot());
                    //check display names
                    assertIterableEquals(EXPECTED_DISPLAY_NAMES, findCountdownDisplayNamesForMeasurementsOf(evaluator));
                    assertIterableEquals(EXPECTED_DISPLAY_NAMES, findMissionDisplayNamesForMeasurementsOf(evaluator));
                    //check exception details
                    assertIterableEquals(EXPECTED_EXCEPTIONS, findExceptionsForMissionFailuresOf(evaluator));
                    assertIterableEquals(EXPECTED_MESSAGES, findThrowableMessagesForMissionFailuresOf(evaluator));
                    forEachNonFilteredStackTraceElementOfMissionFailures(evaluator,
                            e -> Assertions.assertTrue(e.startsWith("com.github.nagyesta"), "Unexpected stack trace: " + e));
                });
    }

}
