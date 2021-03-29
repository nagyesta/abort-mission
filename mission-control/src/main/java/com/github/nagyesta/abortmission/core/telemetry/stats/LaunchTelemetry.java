package com.github.nagyesta.abortmission.core.telemetry.stats;

import java.util.SortedMap;

/**
 * Defines how launch telemetry should look like.
 */
public interface LaunchTelemetry {

    /**
     * Returns the class level data partitioned by class name.
     *
     * @return class data
     */
    SortedMap<String, ClassTelemetry> getClasses();

    /**
     * Returns the aggregated stats based on all data points.
     *
     * @return stats
     */
    AggregatedLaunchStats getStats();

    /**
     * Returns the aggregated stats based on countdown data points only.
     *
     * @return stats
     */
    AggregatedLaunchStats getCountdownStats();

    /**
     * Returns the aggregated stats based on mission data points only.
     *
     * @return stats
     */
    AggregatedLaunchStats getMissionStats();
}
