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
}
