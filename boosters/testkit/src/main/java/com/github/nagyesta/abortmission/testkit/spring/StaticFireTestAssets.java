package com.github.nagyesta.abortmission.testkit.spring;

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

import static com.github.nagyesta.abortmission.core.MissionControl.*;
import static com.github.nagyesta.abortmission.testkit.NoOpMatcher.NOOP;

@SuppressWarnings({"checkstyle:JavadocVariable", "checkstyle:MagicNumber"})
public final class StaticFireTestAssets {

    public static final String PARALLEL = "Parallel";
    public static final String STATIC_FIRE = "StaticFire";
    public static final String BOOSTER = "Booster";
    public static final String CENTER_CORE = "CenterCore";
    public static final String SIDE_BOOSTER = "SideBooster";
    public static final String PER_CLASS = "PerClass";
    public static final ReadOnlyMissionStatistics CENTER_CORE_NOMINAL_STATS =
            new MissionStatisticsCollector(
                    new StageStatisticsCollector(NOOP, 0, 0, 0, 0),
                    new StageStatisticsCollector(NOOP, 0, 0, 1, 0));
    public static final ReadOnlyMissionStatistics SIDE_BOOSTER_NOMINAL_STATS =
            new MissionStatisticsCollector(
                    new StageStatisticsCollector(NOOP, 2, 499, 0, 0),
                    new StageStatisticsCollector(NOOP, 0, 0, 0, 0));
    public static final ReadOnlyMissionStatistics SIDE_BOOSTER_NOMINAL_STATS_PER_CLASS =
            new MissionStatisticsCollector(
                    new StageStatisticsCollector(NOOP, 1, 0, 0, 0),
                    new StageStatisticsCollector(NOOP, 0, 0, 0, 0));
    public static final ReadOnlyMissionStatistics PARALLEL_NOMINAL_STATS =
            new MissionStatisticsCollector(
                    new StageStatisticsCollector(NOOP, 0, 0, 201, 0),
                    new StageStatisticsCollector(NOOP, 0, 0, 400, 0));
    public static final ReadOnlyMissionStatistics PARALLEL_NOMINAL_STATS_PER_CLASS =
            new MissionStatisticsCollector(
                    new StageStatisticsCollector(NOOP, 0, 0, 1, 0),
                    new StageStatisticsCollector(NOOP, 0, 0, 400, 0));
    public static final int DISABLED_CASES = 0;
    public static final int TOTAL_CASES = 503;
    public static final int SUCCESSFUL_CASES = 2;
    public static final int ABORTED_CASES = 499;
    public static final int FAILED_CASES = 2;
    public static final int SUCCESSFUL_PARALLEL_CASES = 400;

    public static final List<String> EXPECTED_EXCEPTIONS = List
            .of("org.springframework.beans.factory.BeanCreationException", "org.springframework.beans.factory.BeanCreationException");
    private static final String MESSAGE = "Error creating bean with name 'sideBooster' defined in "
            + "com.github.nagyesta.abortmission.testkit.spring.StaticFire: Failed to instantiate "
            + "[com.github.nagyesta.abortmission.testkit.spring.Booster]: Factory method 'sideBooster' "
            + "threw exception with message: Side boosters are not supported.";
    public static final List<String> EXPECTED_MESSAGES = List.of(MESSAGE, MESSAGE);

    private StaticFireTestAssets() {
        throw new UnsupportedOperationException("Utility.");
    }


    public static Map<String, Consumer<AbortMissionCommandOps>> getMissionPlan(final DependencyNameExtractor extractor) {
        final Map<String, Consumer<AbortMissionCommandOps>> plan = new HashMap<>();
        plan.put(STATIC_FIRE, ops -> {
            ops.registerHealthCheck(
                    percentageBasedEvaluator(
                            matcher()
                                    .dependencyWith(SIDE_BOOSTER)
                                    .extractor(extractor)
                                    .build())
                            .abortThreshold(10)
                            .burnInTestCount(2).build());
            ops.registerHealthCheck(
                    percentageBasedEvaluator(
                            matcher()
                                    .dependencyWith(CENTER_CORE)
                                    .extractor(extractor)
                                    .build())
                            .build());
        });
        plan.put(PARALLEL, ops -> {
            ops.registerHealthCheck(
                    reportOnlyEvaluator(matcher().anyClass().build()).build());
        });
        return plan;
    }

    public static IntStream staticFireTestParallelInputProvider() {
        return IntStream.range(10, 60);
    }

    public static IntStream staticFireTestInputProvider() {
        return IntStream.range(0, 500);
    }
}

