package com.github.nagyesta.abortmission.booster.jupiter;

import com.github.nagyesta.abortmission.core.MissionControl;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineTestKit;

import java.util.List;
import java.util.stream.IntStream;

import static com.github.nagyesta.abortmission.testkit.LaunchEvaluationUtil.*;
import static com.github.nagyesta.abortmission.testkit.spring.StaticFireTestAssets.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

class StaticFireBoosterTest {

    private static final List<String> EXPECTED_DISPLAY_NAMES_COUNTDOWN_SIDE = IntStream
            .range(0, (int) staticFireTestInputProvider().count() + 1)
            .mapToObj(i -> "StaticFireTestWithSideBoosters")
            .toList();
    private static final List<String> EXPECTED_DISPLAY_NAMES_COUNTDOWN_PER_CLASS = List.of("StaticFireTestWithSideBoostersPerClass");
    private static final List<String> EXPECTED_DISPLAY_NAMES_SIDE = List.of();
    private static final List<String> EXPECTED_DISPLAY_NAMES_CENTER = List.of("testIsOnFire()");

    @Test
    @Tag("integration")
    @SuppressWarnings("checkstyle:MagicNumber")
    void testAssumption() throws NoSuchMethodException {
        EngineTestKit
                .engine("junit-jupiter")
                .selectors(selectClass(StaticFireTestCenterCoreOnly.class),
                        selectClass(StaticFireTestWithSideBoosters.class),
                        selectClass(StaticFireTestWithSideBoostersPerClass.class))
                .execute()
                .testEvents()
                .assertStatistics(stats -> stats
                        .skipped(DISABLED_CASES)
                        .started(TOTAL_CASES)
                        .succeeded(SUCCESSFUL_CASES)
                        .aborted(ABORTED_CASES)
                        .failed(FAILED_CASES));
        MissionControl.matchingHealthChecks(STATIC_FIRE, StaticFireTestWithSideBoosters.class)
                .forEach(evaluator -> {
                    assertEquals(SIDE_BOOSTER_NOMINAL_STATS.getReadOnlyCountdown().getSnapshot(),
                            evaluator.getStats().getReadOnlyCountdown().getSnapshot());
                    assertEquals(SIDE_BOOSTER_NOMINAL_STATS.getReadOnlyMission().getSnapshot(),
                            evaluator.getStats().getReadOnlyMission().getSnapshot());
                    //check display names
                    assertIterableEquals(EXPECTED_DISPLAY_NAMES_COUNTDOWN_SIDE, findCountdownDisplayNamesForMeasurementsOf(evaluator));
                    assertIterableEquals(EXPECTED_DISPLAY_NAMES_SIDE, findMissionDisplayNamesForMeasurementsOf(evaluator));
                    //check exception details
                    assertIterableEquals(List.of(), findExceptionsForMissionFailuresOf(evaluator));
                    assertIterableEquals(List.of(), findThrowableMessagesForMissionFailuresOf(evaluator));
                });
        MissionControl.matchingHealthChecks(STATIC_FIRE, StaticFireTestWithSideBoostersPerClass.class)
                .forEach(evaluator -> {
                    assertEquals(SIDE_BOOSTER_NOMINAL_STATS_PER_CLASS.getReadOnlyCountdown().getSnapshot(),
                            evaluator.getStats().getReadOnlyCountdown().getSnapshot());
                    assertEquals(SIDE_BOOSTER_NOMINAL_STATS_PER_CLASS.getReadOnlyMission().getSnapshot(),
                            evaluator.getStats().getReadOnlyMission().getSnapshot());
                    //check display names
                    assertIterableEquals(EXPECTED_DISPLAY_NAMES_COUNTDOWN_PER_CLASS, findCountdownDisplayNamesForMeasurementsOf(evaluator));
                    assertIterableEquals(EXPECTED_DISPLAY_NAMES_SIDE, findMissionDisplayNamesForMeasurementsOf(evaluator));
                    //check exception details
                    assertIterableEquals(EXPECTED_EXCEPTIONS, findExceptionsForMissionFailuresOf(evaluator));
                    assertIterableEquals(EXPECTED_MESSAGES, findThrowableMessagesForMissionFailuresOf(evaluator));
                    forEachNonFilteredStackTraceElementOfMissionFailures(evaluator,
                            e -> Assertions.assertTrue(e.startsWith("com.github.nagyesta"), "Unexpected stack trace: " + e));
                });
        MissionControl.matchingHealthChecks(STATIC_FIRE, StaticFireTestCenterCoreOnly.class.getDeclaredMethod("testIsOnFire"))
                .forEach(evaluator -> {
                    assertEquals(CENTER_CORE_NOMINAL_STATS.getReadOnlyCountdown().getSnapshot(),
                            evaluator.getStats().getReadOnlyCountdown().getSnapshot());
                    assertEquals(CENTER_CORE_NOMINAL_STATS.getReadOnlyMission().getSnapshot(),
                            evaluator.getStats().getReadOnlyMission().getSnapshot());
                    //check display names
                    assertIterableEquals(List.of(), findCountdownDisplayNamesForMeasurementsOf(evaluator));
                    assertIterableEquals(EXPECTED_DISPLAY_NAMES_CENTER, findMissionDisplayNamesForMeasurementsOf(evaluator));
                    //check exception details
                    assertIterableEquals(List.of(), findExceptionsForMissionFailuresOf(evaluator));
                    assertIterableEquals(List.of(), findThrowableMessagesForMissionFailuresOf(evaluator));
                });
    }

    @Test
    @Tag("integration")
    @SuppressWarnings("checkstyle:MagicNumber")
    void testParallelAssumption() {
        EngineTestKit
                .engine("junit-jupiter")
                .selectors(selectClass(ParallelStaticFireTestWithSideBoostersPerClassTest.class),
                        selectClass(ParallelStaticFireTestWithSideBoostersTest.class))
                .configurationParameter("junit.jupiter.execution.parallel.enabled", "true")
                .configurationParameter("junit.jupiter.execution.parallel.mode.default", "concurrent")
                .configurationParameter("junit.jupiter.execution.parallel.mode.classes.default", "concurrent")
                .configurationParameter("junit.jupiter.execution.parallel.config.strategy", "fixed")
                .configurationParameter("junit.jupiter.execution.parallel.config.fixed.parallelism", "4")
                .execute()
                .testEvents()
                .assertStatistics(stats -> stats
                        .skipped(0)
                        .started(SUCCESSFUL_PARALLEL_CASES)
                        .succeeded(SUCCESSFUL_PARALLEL_CASES)
                        .aborted(0)
                        .failed(0));
        final var threadNamesFromTestMethods = ThreadTracker.THREADS_USED.stream()
                .sorted()
                .toList();
        MissionControl.matchingHealthChecks(PARALLEL, StaticFireTestWithSideBoosters.class)
                .forEach(evaluator -> {
                    assertEquals(PARALLEL_NOMINAL_STATS.getReadOnlyCountdown().getSnapshot(),
                            evaluator.getStats().getReadOnlyCountdown().getSnapshot());
                    assertEquals(PARALLEL_NOMINAL_STATS.getReadOnlyMission().getSnapshot(),
                            evaluator.getStats().getReadOnlyMission().getSnapshot());
                    //check thread names
                    final var capturedThreads = evaluator.getStats().getReadOnlyMission()
                            .timeSeriesStream()
                            .map(StageTimeMeasurement::getThreadName)
                            .distinct()
                            .sorted()
                            .toList();
                    assertIterableEquals(threadNamesFromTestMethods, capturedThreads);
                });
        assertTrue(threadNamesFromTestMethods.size() > 1);
    }

}
