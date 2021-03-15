package com.github.nagyesta.abortmission.testkit.vanilla;

import com.github.nagyesta.abortmission.core.healthcheck.ReadOnlyMissionStatistics;
import com.github.nagyesta.abortmission.core.healthcheck.impl.MissionStatisticsCollector;
import com.github.nagyesta.abortmission.core.healthcheck.impl.StageStatisticsCollector;

import java.util.stream.IntStream;

import static com.github.nagyesta.abortmission.testkit.NoOpMatcher.NOOP;

@SuppressWarnings({"checkstyle:JavadocVariable", "checkstyle:MagicNumber"})
public final class ParachuteDropTestAssets {

    public static final String PER_CLASS_CONTEXT = "parachutePerClass";
    public static final ReadOnlyMissionStatistics PARACHUTE_NOMINAL_STATS =
            new MissionStatisticsCollector(
                    new StageStatisticsCollector(NOOP, 0, 0, 10, 0),
                    new StageStatisticsCollector(NOOP, 1, 6, 3, 0));
    public static final ReadOnlyMissionStatistics PARACHUTE_NOMINAL_STATS_PER_CLASS =
            new MissionStatisticsCollector(
                    new StageStatisticsCollector(NOOP, 0, 0, 1, 0),
                    new StageStatisticsCollector(NOOP, 1, 6, 3, 0));
    public static final int DISABLED_CASES = 0;
    public static final int TOTAL_CASES = 10;
    public static final int SUCCESSFUL_CASES = 3;
    public static final int ABORTED_CASES = 6;
    public static final int FAILED_CASES = 1;

    private ParachuteDropTestAssets() {
        throw new UnsupportedOperationException("Utility.");
    }

    public static IntStream parachuteDropTestInputProvider() {
        return IntStream.rangeClosed(0, 9);
    }
}

