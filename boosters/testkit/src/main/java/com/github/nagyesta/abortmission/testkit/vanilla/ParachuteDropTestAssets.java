package com.github.nagyesta.abortmission.testkit.vanilla;

import com.github.nagyesta.abortmission.core.healthcheck.MissionStatisticsView;
import com.github.nagyesta.abortmission.core.healthcheck.impl.MissionStatisticsCollector;

import java.util.stream.IntStream;

@SuppressWarnings({"checkstyle:JavadocVariable", "checkstyle:MagicNumber"})
public final class ParachuteDropTestAssets {

    public static final MissionStatisticsView PARACHUTE_NOMINAL_STATS =
            new MissionStatisticsCollector(10, 10, 0, 3, 1, 6);
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

