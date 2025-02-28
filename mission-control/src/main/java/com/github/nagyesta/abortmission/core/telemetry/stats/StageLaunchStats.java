package com.github.nagyesta.abortmission.core.telemetry.stats;

import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Contains statistical information about a single stage pf the launch,
 * e.g. post-processing stats related to the same single test class
 * or stats collected related to the same test method (during all calls).
 */
public final class StageLaunchStats {

    private final SortedSet<String> matcherNames;
    private final SortedSet<TestRunTelemetry> timeMeasurements;

    /**
     * Creates a new instance and aggregates statistical data for easy rendering.
     *
     * @param measurements The <b>pre-filtered</b> measurements containing only one data point per test launch.
     * @param matcherNames The names of evaluators matching the stage represented by this instance.
     */
    public StageLaunchStats(final SortedSet<StageTimeMeasurement> measurements, final Set<String> matcherNames) {
        Objects.requireNonNull(measurements, "Measurements cannot be null.");
        Objects.requireNonNull(matcherNames, "MatcherNames cannot be null.");
        this.matcherNames = Collections.unmodifiableSortedSet(new TreeSet<>(matcherNames));
        final var stageTimeMeasurements = new TreeSet<StageTimeMeasurement>(measurements);
        this.timeMeasurements = Collections.unmodifiableSortedSet(stageTimeMeasurements.stream()
                .map(TestRunTelemetry::new)
                .collect(Collectors.toCollection(TreeSet::new)));
    }

    public SortedSet<String> getMatcherNames() {
        return matcherNames;
    }

    public SortedSet<TestRunTelemetry> getTimeMeasurements() {
        return timeMeasurements;
    }

}
