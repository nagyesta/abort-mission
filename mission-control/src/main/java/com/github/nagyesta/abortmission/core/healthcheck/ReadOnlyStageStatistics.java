package com.github.nagyesta.abortmission.core.healthcheck;

import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;

import java.util.stream.Stream;

/**
 * Represents a read-only view of the stage statistics we are capturing.
 */
public interface ReadOnlyStageStatistics {

    /**
     * The snapshot of the collected statistics.
     *
     * @return snapshot
     */
    StageStatisticsSnapshot getSnapshot();

    /**
     * A stream of captured time-series data to allow detailed reporting.
     *
     * @return time series
     */
    Stream<StageTimeMeasurement> timeSeriesStream();
}
