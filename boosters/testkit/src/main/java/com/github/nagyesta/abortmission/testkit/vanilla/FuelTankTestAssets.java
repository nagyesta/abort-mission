package com.github.nagyesta.abortmission.testkit.vanilla;

import com.github.nagyesta.abortmission.core.AbortMissionCommandOps;
import com.github.nagyesta.abortmission.core.healthcheck.ReadOnlyMissionStatistics;
import com.github.nagyesta.abortmission.core.healthcheck.impl.MissionStatisticsCollector;
import com.github.nagyesta.abortmission.core.healthcheck.impl.StageStatisticsCollector;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static com.github.nagyesta.abortmission.core.MissionControl.matcher;
import static com.github.nagyesta.abortmission.core.MissionControl.percentageBasedEvaluator;

@SuppressWarnings({"checkstyle:JavadocVariable", "checkstyle:MagicNumber"})
public final class FuelTankTestAssets {

    public static final String CONTEXT_NAME = "fuelTank";
    public static final String PER_CLASS_CONTEXT = "fuelTankPerClass";
    public static final ReadOnlyMissionStatistics FUEL_TANK_NOMINAL_STATS =
            new MissionStatisticsCollector(
                    new StageStatisticsCollector(0, 0, 5, 0),
                    new StageStatisticsCollector(1, 2, 1, 1));
    public static final ReadOnlyMissionStatistics FUEL_TANK_NOMINAL_STATS_PER_CLASS =
            new MissionStatisticsCollector(
                    new StageStatisticsCollector(0, 0, 1, 0),
                    new StageStatisticsCollector(1, 2, 1, 1));
    public static final int DISABLED_CASES = 0;
    public static final int TOTAL_CASES = 5;
    public static final int SUCCESSFUL_CASES = 1;
    public static final int ABORTED_CASES = 2;
    public static final int FAILED_CASES = 2;

    private FuelTankTestAssets() {
        throw new UnsupportedOperationException("Utility.");
    }

    public static Map<String, Consumer<AbortMissionCommandOps>> getMissionPlan() {
        final Map<String, Consumer<AbortMissionCommandOps>> plan = new HashMap<>();
        plan.put(CONTEXT_NAME, ops -> ops.registerHealthCheck(
                percentageBasedEvaluator(
                        matcher().classNamePattern(".+\\.FuelTankTestContext")
                                .build())
                        .build()));
        plan.put(PER_CLASS_CONTEXT, ops -> ops.registerHealthCheck(
                percentageBasedEvaluator(
                        matcher().classNamePattern(".+\\.FuelTankTestContextPerClass")
                                .build())
                        .build()));
        return plan;
    }

    public static IntStream fuelTankTestInputProvider() {
        return IntStream.of(-1, 42, 5001, 420000, 500000);
    }
}

