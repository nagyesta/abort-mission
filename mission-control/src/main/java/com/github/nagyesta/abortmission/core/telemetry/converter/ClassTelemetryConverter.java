package com.github.nagyesta.abortmission.core.telemetry.converter;

import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import com.github.nagyesta.abortmission.core.telemetry.stats.AggregatedLaunchStats;
import com.github.nagyesta.abortmission.core.telemetry.stats.StageLaunchStats;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Converter implementation that allows simplified processing of time series data on a test class level.
 */
public class ClassTelemetryConverter {

    /**
     * Partitions time measurements to method level buckets.
     *
     * @param measurements The measurements of all methods.
     * @return The map of per method measurements.
     */
    public Map<String, List<StageTimeMeasurement>> partitionByMethods(final Collection<StageTimeMeasurement> measurements) {
        Objects.requireNonNull(measurements, "Measurements cannot be null.");
        return measurements.stream()
                .collect(Collectors.groupingBy(StageTimeMeasurement::getTestCaseId));
    }

    /**
     * Processes statistical information of all test methods in the class based on partitioned data.
     *
     * @param matcherNames The names of matchers partitioned per method name.
     * @param byMethods    The time series information per method name.
     * @return The parsed statistics of each method.
     */
    public Map<String, StageLaunchStats> processLaunchStats(final Map<String, Set<String>> matcherNames,
                                                            final Map<String, List<StageTimeMeasurement>> byMethods) {
        Objects.requireNonNull(matcherNames, "MatcherNames cannot be null.");
        Objects.requireNonNull(byMethods, "ByMethods cannot be null.");
        final Map<String, StageLaunchStats> methodStats = new TreeMap<>();
        byMethods.forEach((method, measurementList) -> {
            if (StageTimeMeasurement.CLASS_ONLY.equals(method)) {
                return;
            }
            final Set<String> names = matcherNames.getOrDefault(method, Collections.emptySet());
            methodStats.put(method, new StageLaunchStats(AggregatedLaunchStats.filter(measurementList), names));
        });
        return Collections.unmodifiableMap(methodStats);
    }

    /**
     * Processes statistical information of test instance post-processing of the given class based on partitioned data.
     *
     * @param matcherNames The names of matchers partitioned per method name.
     * @param byMethods    The time series information per method name.
     * @return The parsed statistics of the test instance post-processing.
     */
    public StageLaunchStats processCountdownStats(final Map<String, Set<String>> matcherNames,
                                                  final Map<String, List<StageTimeMeasurement>> byMethods) {
        Objects.requireNonNull(matcherNames, "MatcherNames cannot be null.");
        Objects.requireNonNull(byMethods, "ByMethods cannot be null.");
        final List<StageTimeMeasurement> classMeasurements = byMethods
                .getOrDefault(StageTimeMeasurement.CLASS_ONLY, Collections.emptyList());
        final Set<String> classMatcherNames = matcherNames
                .getOrDefault(StageTimeMeasurement.CLASS_ONLY, Collections.emptySet());
        return new StageLaunchStats(AggregatedLaunchStats.filter(classMeasurements), classMatcherNames);
    }

    /**
     * Merges instance post-processing and test method stats into a single aggregation to have a class level total.
     *
     * @param countdownStats The post-processing stats.
     * @param launchSet      The test method level statistics.
     * @return The merged class total.
     */
    public AggregatedLaunchStats summarizeDescendantStats(final StageLaunchStats countdownStats,
                                                          final Collection<StageLaunchStats> launchSet) {
        Objects.requireNonNull(countdownStats, "CountdownStats cannot be null.");
        Objects.requireNonNull(launchSet, "LaunchSet cannot be null.");
        final Set<AggregatedLaunchStats> allStats = launchSet.stream()
                .map(StageLaunchStats::getStats).collect(Collectors.toSet());
        allStats.add(countdownStats.getStats());
        return new AggregatedLaunchStats(allStats);
    }
}
