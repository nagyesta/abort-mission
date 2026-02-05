package com.github.nagyesta.abortmission.booster.jupiter;

import com.github.nagyesta.abortmission.core.MissionControl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineTestKit;

import java.util.List;

import static com.github.nagyesta.abortmission.testkit.LaunchEvaluationUtil.*;
import static com.github.nagyesta.abortmission.testkit.vanilla.WeatherTestAssets.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

class WeatherTest {

    private static final String WEATHER_TEST_CONTEXT = "WeatherTestContext";
    private static final List<String> EXPECTED_DISPLAY_NAMES_COUNTDOWN_WEATHER = weatherTestInputProvider()
            .mapToObj(i -> WEATHER_TEST_CONTEXT)
            .toList();
    private static final List<String> EXPECTED_DISPLAY_NAMES_COUNTDOWN_LAUNCH = List.of(WEATHER_TEST_CONTEXT, WEATHER_TEST_CONTEXT);

    private static final List<String> EXPECTED_DISPLAY_NAMES_WEATHER = List.of(
            "testLaunchShouldBeDelayedWhenAStormIsLikely(int) [1] 1",
            "testLaunchShouldBeDelayedWhenAStormIsLikely(int) [2] 42",
            "testLaunchShouldBeDelayedWhenAStormIsLikely(int) [3] 70",
            "testLaunchShouldBeDelayedWhenAStormIsLikely(int) [4] 100");

    private static final List<String> EXPECTED_DISPLAY_NAMES_LAUNCH = List.of(
            "testLaunchShouldIgnoreWeatherPrediction",
            "testLaunchShouldNotCheckWeather");

    @Test
    @Tag("integration")
    @SuppressWarnings("checkstyle:MagicNumber")
    void testAssumption() {
        EngineTestKit
                .engine("junit-jupiter")
                .selectors(selectClass(WeatherTestContext.class))
                .execute()
                .testEvents()
                .assertStatistics(stats -> stats
                        .skipped(DISABLED_CASES)
                        .started(TOTAL_CASES)
                        .succeeded(SUCCESSFUL_CASES)
                        .aborted(ABORTED_CASES)
                        .failed(FAILED_CASES));
        MissionControl.matchingHealthChecks(CONTEXT_NAME, WeatherTestContext.class)
                .forEach(evaluator -> {
                    if (evaluator.getMatcher().getName().contains("Launch")) {
                        assertEquals(LAUNCH_STATS.getReadOnlyCountdown().getSnapshot(),
                                evaluator.getStats().getReadOnlyCountdown().getSnapshot());
                        assertEquals(LAUNCH_STATS.getReadOnlyMission().getSnapshot(),
                                evaluator.getStats().getReadOnlyMission().getSnapshot());
                        //check display names
                        assertIterableEquals(EXPECTED_DISPLAY_NAMES_COUNTDOWN_LAUNCH,
                                findCountdownDisplayNamesForMeasurementsOf(evaluator));
                        assertIterableEquals(EXPECTED_DISPLAY_NAMES_LAUNCH,
                                findMissionDisplayNamesForMeasurementsOf(evaluator));
                        //check exception details
                        assertIterableEquals(EXPECTED_MESSAGES, findThrowableMessagesForMissionFailuresOf(evaluator));
                        forEachNonFilteredStackTraceElementOfMissionFailures(evaluator,
                                e -> Assertions.assertTrue(e.startsWith("com.github.nagyesta"), "Unexpected stack trace: " + e));
                    } else {
                        assertEquals(WEATHER_STATS.getReadOnlyCountdown().getSnapshot(),
                                evaluator.getStats().getReadOnlyCountdown().getSnapshot());
                        assertEquals(WEATHER_STATS.getReadOnlyMission().getSnapshot(),
                                evaluator.getStats().getReadOnlyMission().getSnapshot());
                        //check display names
                        assertIterableEquals(EXPECTED_DISPLAY_NAMES_COUNTDOWN_WEATHER,
                                findCountdownDisplayNamesForMeasurementsOf(evaluator));
                        assertIterableEquals(EXPECTED_DISPLAY_NAMES_WEATHER,
                                findMissionDisplayNamesForMeasurementsOf(evaluator));
                        //check exception details
                        assertIterableEquals(EXPECTED_MESSAGES, findThrowableMessagesForMissionFailuresOf(evaluator));
                        forEachNonFilteredStackTraceElementOfMissionFailures(evaluator,
                                e -> Assertions.assertTrue(e.startsWith("com.github.nagyesta"), "Unexpected stack trace: " + e));
                    }
                });
    }

}
