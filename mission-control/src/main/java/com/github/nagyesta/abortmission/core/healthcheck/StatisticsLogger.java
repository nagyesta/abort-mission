package com.github.nagyesta.abortmission.core.healthcheck;

import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;

public interface StatisticsLogger {

    /**
     * Increases the number counting failures.
     */
    void incrementFailed();

    /**
     * Increases the number counting abortions.
     */
    void incrementAborted();

    /**
     * Increases the number counting successes.
     */
    void incrementSucceeded();

    /**
     * Increases the number counting suppression.
     */
    void incrementSuppressed();

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
    void logAndIncrement(StageTimeMeasurement timeMeasurement);
}
