package com.github.nagyesta.abortmission.core.telemetry.stats;

import java.util.SortedMap;

/**
 * Data source interface that allows simplified access/processing of time series data.
 */
public interface LaunchTelemetryDataSource {

    /**
     * Returns the data partitioned per test class and calculates class level statistics.
     *
     * @return The per class statistical data.
     */
    SortedMap<String, ClassTelemetry> fetchClassStatistics();
}
