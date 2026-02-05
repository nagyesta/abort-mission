package com.github.nagyesta.abortmission.testkit.vanilla;

import com.github.nagyesta.abortmission.core.AbortMissionCommandOps;
import com.github.nagyesta.abortmission.core.extractor.DependencyNameExtractor;
import com.github.nagyesta.abortmission.core.healthcheck.ReadOnlyMissionStatistics;
import com.github.nagyesta.abortmission.core.healthcheck.impl.MissionStatisticsCollector;
import com.github.nagyesta.abortmission.core.healthcheck.impl.StageStatisticsCollector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static com.github.nagyesta.abortmission.core.MissionControl.matcher;
import static com.github.nagyesta.abortmission.core.MissionControl.percentageBasedEvaluator;
import static com.github.nagyesta.abortmission.testkit.NoOpMatcher.NOOP;

@SuppressWarnings({"checkstyle:JavadocVariable", "checkstyle:MagicNumber"})
public final class WeatherTestAssets {

    public static final String CONTEXT_NAME = "weather";
    public static final ReadOnlyMissionStatistics WEATHER_STATS_PER_CLASS =
            new MissionStatisticsCollector(
                    new StageStatisticsCollector(NOOP, 0, 0, 1, 0),
                    new StageStatisticsCollector(NOOP, 1, 1, 2, 0));
    public static final ReadOnlyMissionStatistics LAUNCH_STATS_PER_CLASS =
            new MissionStatisticsCollector(
                    new StageStatisticsCollector(NOOP, 0, 0, 1, 0),
                    new StageStatisticsCollector(NOOP, 0, 2, 0, 0));
    public static final ReadOnlyMissionStatistics WEATHER_STATS =
            new MissionStatisticsCollector(
                    new StageStatisticsCollector(NOOP, 0, 0, 4, 0),
                    new StageStatisticsCollector(NOOP, 1, 1, 2, 0));
    public static final ReadOnlyMissionStatistics LAUNCH_STATS =
            new MissionStatisticsCollector(
                    new StageStatisticsCollector(NOOP, 0, 0, 2, 0),
                    new StageStatisticsCollector(NOOP, 0, 2, 0, 0));
    public static final int DISABLED_CASES = 0;
    public static final int TOTAL_CASES = 6;
    public static final int SUCCESSFUL_CASES = 2;
    public static final int ABORTED_CASES = 3;
    public static final int FAILED_CASES = 1;

    public static final List<String> EXPECTED_MESSAGES = List.of("Something is wrong. ==> expected: <true> but was: <false>");
    private WeatherTestAssets() {
        throw new UnsupportedOperationException("Utility.");
    }

    public static Map<String, Consumer<AbortMissionCommandOps>> getMissionPlan(final DependencyNameExtractor extractor) {
        final Map<String, Consumer<AbortMissionCommandOps>> plan = new HashMap<>();
        final var weatherContextMatcher = matcher().classNamePattern(".+\\.WeatherTestContext").build();
        final var weatherEvaluator = percentageBasedEvaluator(
                matcher()
                        .and(weatherContextMatcher)
                        .andAtLast(matcher().dependencyWith("weather").extractor(extractor).build())
                        .build())
                .abortThreshold(1)
                .build();
        final var launchEvaluator = percentageBasedEvaluator(
                matcher()
                        .and(weatherContextMatcher)
                        .andAtLast(matcher().dependencyWith("launch").extractor(extractor).build())
                        .build())
                .dependsOn(weatherEvaluator)
                .build();
        plan.put(CONTEXT_NAME, ops -> {
            ops.registerHealthCheck(weatherEvaluator);
            ops.registerHealthCheck(launchEvaluator);
        });
        return plan;
    }

    public static IntStream weatherTestInputProvider() {
        return IntStream.of(1, 42, 70, 100);
    }
}

