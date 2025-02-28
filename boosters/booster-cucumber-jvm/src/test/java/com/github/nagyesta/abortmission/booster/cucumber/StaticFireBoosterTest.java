package com.github.nagyesta.abortmission.booster.cucumber;

import com.github.nagyesta.abortmission.booster.cucumber.staticfire.StaticFireTestContext;
import com.github.nagyesta.abortmission.core.AbortMissionGlobalConfiguration;
import com.github.nagyesta.abortmission.core.MissionControl;
import com.github.nagyesta.abortmission.core.telemetry.StageResult;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineTestKit;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.nagyesta.abortmission.testkit.LaunchEvaluationUtil.*;
import static com.github.nagyesta.abortmission.testkit.spring.StaticFireTestAssets.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class StaticFireBoosterTest {

    private static final String CENTER_RESOURCE =
            "classpath:com/github/nagyesta/abortmission/booster/cucumber/staticfire/StaticFireCenterCoreOnly.feature";
    private static final String HEAVY_RESOURCE =
            "classpath:com/github/nagyesta/abortmission/booster/cucumber/staticfire/StaticFireHeavy.feature";
    private static final int FIRST_LINE_OF_EXAMPLES_SIDE = 12;
    private static final int LAST_LINE_OF_EXAMPLES_SIDE = 511;
    private static final List<String> EXPECTED_CENTER_DISPLAY_NAMES = List
            .of("StaticFire_1 Center core static fire is burning (" + CENTER_RESOURCE + ":10)");
    private static final List<String> EXPECTED_SIDE_DISPLAY_NAMES = Stream.of(
                    IntStream.rangeClosed(FIRST_LINE_OF_EXAMPLES_SIDE, LAST_LINE_OF_EXAMPLES_SIDE)
                            .mapToObj(i -> "StaticFire_3 Side booster static fire is burning (" + HEAVY_RESOURCE + ":" + i + ")"),
                    Stream.of("StaticFire_4 Center core and side booster static fire is burning (" + HEAVY_RESOURCE + ":515)")
            )
            .flatMap(Function.identity())
            .collect(Collectors.toList());

    @Test
    @Tag("integration")
    @SuppressWarnings("checkstyle:MagicNumber")
    public void testAssumption() {
        AbortMissionGlobalConfiguration.shared().setStackTraceFilter(e -> !e.getClassName().contains("org.springframework"));
        EngineTestKit
                .engine("junit-vintage")
                .selectors(selectClass(StaticFireTestContext.class))
                .execute()
                .testEvents()
                .assertStatistics(stats -> stats
                        .skipped(DISABLED_CASES)
                        .started(TOTAL_CASES)
                        .succeeded(SUCCESSFUL_CASES)
                        .aborted(ABORTED_CASES)
                        .failed(FAILED_CASES));
        MissionControl.commandOps(STATIC_FIRE).allEvaluators().forEach(evaluator -> {
            if (evaluator.getMatcher().getName().contains(CENTER_CORE)) {
                assertEquals(CENTER_CORE_NOMINAL_STATS.getReadOnlyMission().getSnapshot(),
                        evaluator.getStats().getReadOnlyMission().getSnapshot());
                //check display names
                final var actualCountdownNames = evaluator.getStats().getReadOnlyCountdown().timeSeriesStream()
                        .map(StageTimeMeasurement::getDisplayName)
                        .collect(Collectors.toList());
                assertIterableEquals(EXPECTED_CENTER_DISPLAY_NAMES, actualCountdownNames);
                final var actualMissionNames = evaluator.getStats().getReadOnlyMission().timeSeriesStream()
                        .map(StageTimeMeasurement::getDisplayName)
                        .collect(Collectors.toList());
                assertIterableEquals(EXPECTED_CENTER_DISPLAY_NAMES, actualMissionNames);
                //check exception details
                final var actualExceptions = evaluator.getStats().getReadOnlyMission().timeSeriesStream()
                        .filter(s -> s.getResult() == StageResult.FAILURE)
                        .map(StageTimeMeasurement::getThrowableClass)
                        .collect(Collectors.toList());
                final var actualMessages = evaluator.getStats().getReadOnlyMission().timeSeriesStream()
                        .filter(s -> s.getResult() == StageResult.FAILURE)
                        .map(StageTimeMeasurement::getThrowableMessage)
                        .collect(Collectors.toList());
                assertIterableEquals(Collections.emptyList(), actualExceptions);
                assertIterableEquals(Collections.emptyList(), actualMessages);
            } else {
                //As there is no countdown abort here, we need to compare the regular countdown with our mission stats
                assertEquals(SIDE_BOOSTER_NOMINAL_STATS.getReadOnlyCountdown().getSnapshot(),
                        evaluator.getStats().getReadOnlyMission().getSnapshot());
                //check display names
                assertIterableEquals(EXPECTED_SIDE_DISPLAY_NAMES, findCountdownDisplayNamesForMeasurementsOf(evaluator));
                assertIterableEquals(EXPECTED_SIDE_DISPLAY_NAMES, findMissionDisplayNamesForMeasurementsOf(evaluator));
                //check exception details
                assertIterableEquals(EXPECTED_EXCEPTIONS, findExceptionsForMissionFailuresOf(evaluator));
                assertIterableEquals(EXPECTED_MESSAGES, findThrowableMessagesForMissionFailuresOf(evaluator));
                forEachNonFilteredStackTraceElementOfMissionFailures(evaluator,
                        e -> Assertions.assertTrue(e.startsWith("com.github.nagyesta"), "Unexpected stack trace: " + e));
            }
        });
    }

}
