package com.github.nagyesta.abortmission.core.telemetry.stats;

import com.github.nagyesta.abortmission.core.AbortMissionCommandOps;
import com.github.nagyesta.abortmission.core.telemetry.converter.LaunchTelemetryConverter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents the root of the execution related measurement and statistical information
 * both summed up on total and per test class.
 */
public final class LaunchTelemetry {

    private final SortedMap<String, ClassTelemetry> classes;
    private final AggregatedLaunchStats countdownStats;
    private final AggregatedLaunchStats missionStats;
    private final AggregatedLaunchStats stats;

    /**
     * Creates a new instance and parses the nameSpaces we have used.
     *
     * @param converter  The converter we are using to help processing the data.
     * @param nameSpaces The namespaces used during test execution,
     */
    public LaunchTelemetry(final LaunchTelemetryConverter converter,
                           final Map<String, AbortMissionCommandOps> nameSpaces) {
        Objects.requireNonNull(converter, "Converter cannot be null.");
        Objects.requireNonNull(nameSpaces, "Namespaces cannot be null.");
        this.classes = converter.processClassStatistics(nameSpaces);
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

    /**
     * Collects the namespace information from {@link AbortMissionCommandOps} and calls
     * the constructor with the results.
     *
     * @return the collected launch telemetry.
     */
    public static LaunchTelemetry collect() {
        return new LaunchTelemetry(new LaunchTelemetryConverter(), resolveContextMap());
    }

    protected static Map<String, AbortMissionCommandOps> resolveContextMap() {
        final Map<Boolean, List<String>> contextNames = AbortMissionCommandOps.contextNames().stream()
                .collect(Collectors.partitioningBy(String::isEmpty));
        final Map<String, AbortMissionCommandOps> nameSpaces = new TreeMap<>();
        contextNames.getOrDefault(false, Collections.emptyList()).forEach(s -> nameSpaces.put(s, AbortMissionCommandOps.named(s)));
        if (contextNames.containsKey(true) && !contextNames.get(true).isEmpty()) {
            nameSpaces.put("", AbortMissionCommandOps.shared());
        }
        return nameSpaces;
    }

    public SortedMap<String, ClassTelemetry> getClasses() {
        return classes;
    }

    public AggregatedLaunchStats getStats() {
        return stats;
    }

    public AggregatedLaunchStats getCountdownStats() {
        return countdownStats;
    }

    public AggregatedLaunchStats getMissionStats() {
        return missionStats;
    }
}
