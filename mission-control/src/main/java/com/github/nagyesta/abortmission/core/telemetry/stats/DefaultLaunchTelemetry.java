package com.github.nagyesta.abortmission.core.telemetry.stats;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Base class containing the common parts of {@link LaunchTelemetry} implementations.
 */
public class DefaultLaunchTelemetry implements LaunchTelemetry {

    private final SortedMap<String, ClassTelemetry> classes;
    private final AggregatedLaunchStats countdownStats;
    private final AggregatedLaunchStats missionStats;
    private final AggregatedLaunchStats stats;

    /**
     * Creates a new instance and aggregates the available measurements from the sta source.
     *
     * @param dataSource The data source for accessing the measurements.
     */
    public DefaultLaunchTelemetry(final LaunchTelemetryDataSource dataSource) {
        Objects.requireNonNull(dataSource, "DataSource cannot be null.");
        this.classes = dataSource.fetchClassStatistics();
        this.countdownStats = new AggregatedLaunchStats(classes.values().stream()
                .map(ClassTelemetry::getCountdown)
                .map(StageLaunchStats::getStats)
                .collect(Collectors.toSet()));
        this.missionStats = new AggregatedLaunchStats(classes.values().stream()
                .map(ClassTelemetry::getLaunches)
                .map(Map::values)
                .flatMap(Collection::stream)
                .map(StageLaunchStats::getStats)
                .collect(Collectors.toSet()));
        this.stats = new AggregatedLaunchStats(new HashSet<>(Arrays.asList(countdownStats, missionStats)));
    }

    @Override
    public SortedMap<String, ClassTelemetry> getClasses() {
        return classes;
    }

    @Override
    public AggregatedLaunchStats getStats() {
        return stats;
    }

    @Override
    public AggregatedLaunchStats getCountdownStats() {
        return countdownStats;
    }

    @Override
    public AggregatedLaunchStats getMissionStats() {
        return missionStats;
    }
}
