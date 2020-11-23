package com.github.nagyesta.abortmission.core.healthcheck;

import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;

import java.util.stream.Stream;

/**
 * Represents a read-only view of the stage statistics we are capturing.
 */
public interface ReadOnlyStageStatistics {

    /**
     * The total number of successful/failed/aborted executions.
     *
     * @return success + fail + abort
     */
    int getTotal();

    /**
     * The number of failed or aborted executions.
     *
     * @return fail + abort
     */
    int getNotSuccessful();

    /**
     * The number of failed executions.
     *
     * @return fail
     */
    int getFailed();

    /**
     * The number of aborted executions.
     *
     * @return abort
     */
    int getAborted();

    /**
     * The number of successful executions.
     *
     * @return success
     */
    int getSucceeded();

    /**
     * The number of suppressed (failure/abort logging) cases.
     *
     * @return suppressed
     */
    int getSuppressed();

    /**
     * A stream of captured time-series data to allow detailed reporting.
     *
     * @return time series
     */
    Stream<StageTimeMeasurement> timeSeriesStream();
}
