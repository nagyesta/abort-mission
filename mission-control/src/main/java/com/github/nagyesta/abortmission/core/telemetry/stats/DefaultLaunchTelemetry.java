package com.github.nagyesta.abortmission.core.telemetry.stats;

import java.util.Objects;
import java.util.SortedMap;

/**
 * Base class containing the common parts of {@link LaunchTelemetry} implementations.
 */
public class DefaultLaunchTelemetry implements LaunchTelemetry {

    private final SortedMap<String, ClassTelemetry> classes;

    /**
     * Creates a new instance and aggregates the available measurements from the sta source.
     *
     * @param dataSource The data source for accessing the measurements.
     */
    public DefaultLaunchTelemetry(final LaunchTelemetryDataSource dataSource) {
        Objects.requireNonNull(dataSource, "DataSource cannot be null.");
        this.classes = dataSource.fetchClassStatistics();
    }

    @Override
    public SortedMap<String, ClassTelemetry> getClasses() {
        return classes;
    }
}
