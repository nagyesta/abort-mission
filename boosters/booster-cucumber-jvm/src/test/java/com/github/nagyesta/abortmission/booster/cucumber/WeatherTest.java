package com.github.nagyesta.abortmission.booster.cucumber;

import com.github.nagyesta.abortmission.booster.cucumber.weather.WeatherTestContext;
import com.github.nagyesta.abortmission.core.MissionControl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineTestKit;

import java.util.List;
import java.util.stream.IntStream;

import static com.github.nagyesta.abortmission.testkit.LaunchEvaluationUtil.*;
import static com.github.nagyesta.abortmission.testkit.vanilla.WeatherTestAssets.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

class WeatherTest {

    private static final int FIRST_LINE_OF_EXAMPLES = 11;
    private static final int LAST_LINE_OF_EXAMPLES = 14;
    private static final String RESOURCE = "classpath:com/github/nagyesta/abortmission/booster/cucumber/weather/Weather.feature";
    private static final List<String> EXPECTED_DISPLAY_NAMES_WEATHER = IntStream.rangeClosed(FIRST_LINE_OF_EXAMPLES, LAST_LINE_OF_EXAMPLES)
            .mapToObj(i -> "Weather_1 We should launch when there is no storm (" + RESOURCE + ":" + i + ")")
            .toList();
    private static final List<String> EXPECTED_DISPLAY_NAMES_LAUNCH =
            List.of("Launch_1 We ignore the weather (" + RESOURCE + ":17)",
                    "Launch_2 Weather who? (" + RESOURCE + ":23)");

    @Test
    @Tag("integration")
    @SuppressWarnings("checkstyle:MagicNumber")
    void testAssumption() {
        EngineTestKit
                .engine("junit-vintage")
                .selectors(selectClass(WeatherTestContext.class))
                .execute()
                .testEvents()
                .assertStatistics(stats -> stats
                        .skipped(DISABLED_CASES)
                        .started(TOTAL_CASES)
                        .succeeded(SUCCESSFUL_CASES)
                        .aborted(ABORTED_CASES)
                        .failed(FAILED_CASES));
        MissionControl.commandOps(CONTEXT_NAME).allEvaluators()
                .forEach(evaluator -> {
                    if (evaluator.getMatcher().getName().contains("Weather")) {
                        assertEquals(WEATHER_STATS.getReadOnlyMission().getSnapshot(),
                                evaluator.getStats().getReadOnlyMission().getSnapshot());
                        //check display names
                        assertIterableEquals(EXPECTED_DISPLAY_NAMES_WEATHER, findCountdownDisplayNamesForMeasurementsOf(evaluator));
                        assertIterableEquals(EXPECTED_DISPLAY_NAMES_WEATHER, findMissionDisplayNamesForMeasurementsOf(evaluator));
                        //check exception details
                        assertIterableEquals(EXPECTED_MESSAGES, findThrowableMessagesForMissionFailuresOf(evaluator));
                        forEachNonFilteredStackTraceElementOfMissionFailures(evaluator,
                                e -> Assertions.assertTrue(e.startsWith("com.github.nagyesta"), "Unexpected stack trace: " + e));
                    } else {
                        assertEquals(LAUNCH_STATS.getReadOnlyMission().getSnapshot(),
                                evaluator.getStats().getReadOnlyMission().getSnapshot());
                        //check display names
                        assertIterableEquals(EXPECTED_DISPLAY_NAMES_LAUNCH, findCountdownDisplayNamesForMeasurementsOf(evaluator));
                        assertIterableEquals(EXPECTED_DISPLAY_NAMES_LAUNCH, findMissionDisplayNamesForMeasurementsOf(evaluator));
                        //check exception details
                        assertIterableEquals(List.of(), findThrowableMessagesForMissionFailuresOf(evaluator));
                        forEachNonFilteredStackTraceElementOfMissionFailures(evaluator,
                                e -> Assertions.assertTrue(e.startsWith("com.github.nagyesta"), "Unexpected stack trace: " + e));
                    }
                });
    }

}
