package com.github.nagyesta.abortmission.booster.jupiter;

import com.github.nagyesta.abortmission.core.MissionControl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineTestKit;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.nagyesta.abortmission.testkit.LaunchEvaluationUtil.*;
import static com.github.nagyesta.abortmission.testkit.vanilla.ParachuteDropTestAssets.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class ParachuteDropTest {

    private static final List<String> EXPECTED_DISPLAY_NAMES_COUNTDOWN = parachuteDropTestInputProvider()
            .mapToObj(i -> "ParachuteTestContext")
            .collect(Collectors.toUnmodifiableList());
    private static final List<String> EXPECTED_DISPLAY_NAMES_COUNTDOWN_PER_CLASS = List.of("ParachuteTestContextPerClass");

    @SuppressWarnings("checkstyle:MagicNumber")
    private static final List<String> EXPECTED_DISPLAY_NAMES = IntStream.range(0, 10)
            .mapToObj(i -> "testParachuteShouldOpenWhenCalled(int) [" + (i + 1) + "] " + i)
            .collect(Collectors.toList());

    @Test
    @Tag("integration")
    @SuppressWarnings("checkstyle:MagicNumber")
    public void testAssumption() {
        EngineTestKit
                .engine("junit-jupiter")
                .selectors(selectClass(ParachuteTestContext.class))
                .execute()
                .testEvents()
                .assertStatistics(stats -> stats
                        .skipped(DISABLED_CASES)
                        .started(TOTAL_CASES)
                        .succeeded(SUCCESSFUL_CASES)
                        .aborted(ABORTED_CASES)
                        .failed(FAILED_CASES));
        MissionControl.matchingHealthChecks(ParachuteTestContext.class)
                .forEach(evaluator -> {
                    assertEquals(PARACHUTE_NOMINAL_STATS.getReadOnlyCountdown().getSnapshot(),
                            evaluator.getStats().getReadOnlyCountdown().getSnapshot());
                    assertEquals(PARACHUTE_NOMINAL_STATS.getReadOnlyMission().getSnapshot(),
                            evaluator.getStats().getReadOnlyMission().getSnapshot());
                    //check display names
                    assertIterableEquals(EXPECTED_DISPLAY_NAMES_COUNTDOWN, findCountdownDisplayNamesForMeasurementsOf(evaluator));
                    assertIterableEquals(EXPECTED_DISPLAY_NAMES, findMissionDisplayNamesForMeasurementsOf(evaluator));
                    //check exception details
                    assertIterableEquals(EXPECTED_EXCEPTIONS, findExceptionsForMissionFailuresOf(evaluator));
                    assertIterableEquals(EXPECTED_MESSAGES, findThrowableMessagesForMissionFailuresOf(evaluator));
                    forEachNonFilteredStackTraceElementOfMissionFailures(evaluator,
                            e -> Assertions.assertTrue(e.startsWith("com.github.nagyesta"), "Unexpected stack trace: " + e));
                });
    }

    @Test
    @Tag("integration")
    @SuppressWarnings("checkstyle:MagicNumber")
    public void testAssumptionPerClass() {
        EngineTestKit
                .engine("junit-jupiter")
                .selectors(selectClass(ParachuteTestContextPerClass.class))
                .execute()
                .testEvents()
                .assertStatistics(stats -> stats
                        .skipped(DISABLED_CASES)
                        .started(TOTAL_CASES)
                        .succeeded(SUCCESSFUL_CASES)
                        .aborted(ABORTED_CASES)
                        .failed(FAILED_CASES));
        MissionControl.matchingHealthChecks(PER_CLASS_CONTEXT, ParachuteTestContextPerClass.class)
                .forEach(evaluator -> {
                    assertEquals(PARACHUTE_NOMINAL_STATS_PER_CLASS.getReadOnlyCountdown().getSnapshot(),
                            evaluator.getStats().getReadOnlyCountdown().getSnapshot());
                    assertEquals(PARACHUTE_NOMINAL_STATS_PER_CLASS.getReadOnlyMission().getSnapshot(),
                            evaluator.getStats().getReadOnlyMission().getSnapshot());
                    //check display names
                    assertIterableEquals(EXPECTED_DISPLAY_NAMES_COUNTDOWN_PER_CLASS, findCountdownDisplayNamesForMeasurementsOf(evaluator));
                    assertIterableEquals(EXPECTED_DISPLAY_NAMES, findMissionDisplayNamesForMeasurementsOf(evaluator));
                    //check exception details
                    assertIterableEquals(EXPECTED_EXCEPTIONS, findExceptionsForMissionFailuresOf(evaluator));
                    assertIterableEquals(EXPECTED_MESSAGES, findThrowableMessagesForMissionFailuresOf(evaluator));
                    forEachNonFilteredStackTraceElementOfMissionFailures(evaluator,
                            e -> Assertions.assertTrue(e.startsWith("com.github.nagyesta"), "Unexpected stack trace: " + e));
                });
    }

}
