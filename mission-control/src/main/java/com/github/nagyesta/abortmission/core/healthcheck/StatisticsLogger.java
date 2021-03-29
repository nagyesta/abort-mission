package com.github.nagyesta.abortmission.core.healthcheck;

import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;

public interface StatisticsLogger {

    /**
     * Captures the measurement instance provided for reporting purposes.
     *
     * @param timeMeasurement The measurement we observed.
     */
    void logTimeMeasurement(StageTimeMeasurement timeMeasurement);

    /**
     * Captures the measurement instance provided for reporting purposes and automatically increases the relevant counter.
     * Shortcut to {@link #logTimeMeasurement(StageTimeMeasurement)} and one of the increment.* methods.
     *
     * @param timeMeasurement The measurement we observed.
     */
    default void logAndIncrement(final StageTimeMeasurement timeMeasurement) {
        logTimeMeasurement(timeMeasurement);
    }
}
