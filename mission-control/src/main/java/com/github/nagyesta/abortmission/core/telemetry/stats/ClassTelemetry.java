package com.github.nagyesta.abortmission.core.telemetry.stats;

import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import com.github.nagyesta.abortmission.core.telemetry.converter.ClassTelemetryConverter;

import java.util.*;

/**
 * Contains measurement data and statistics related to a single test class.
 */
public final class ClassTelemetry {

    private final String className;
    private final StageLaunchStats countdown;
    private final AggregatedLaunchStats stats;
    private final Map<String, StageLaunchStats> launches;

    /**
     * Creates a new instance and evaluates the measurements we collected for the class.
     *
     * @param converter    The converter we are using to help to process the data.
     * @param className    The fully qualified name of the test class.
     * @param measurements The measurements we recorded during test run.
     * @param matcherNames The matchers which were identified during run, collected for
     *                     each methodName of the class (including instance port-processing
     *                     as a special method).
     */
    public ClassTelemetry(final ClassTelemetryConverter converter,
                          final String className,
                          final Collection<StageTimeMeasurement> measurements,
                          final Map<String, Set<String>> matcherNames) {
        Objects.requireNonNull(converter, "Converter cannot be null.");
        Objects.requireNonNull(measurements, "Measurements cannot be null.");
        Objects.requireNonNull(matcherNames, "MatcherNames cannot be null.");
        this.className = Objects.requireNonNull(className, "Class name cannot be null.");
        final Map<String, List<StageTimeMeasurement>> byMethods = converter.partitionByMethods(measurements);
        this.countdown = converter.processCountdownStats(matcherNames, byMethods);
        this.launches = converter.processLaunchStats(matcherNames, byMethods);
        this.stats = converter.summarizeDescendantStats(countdown, launches.values());
    }

    public String getClassName() {
        return className;
    }

    public StageLaunchStats getCountdown() {
        return countdown;
    }

    public AggregatedLaunchStats getStats() {
        return stats;
    }

    public Map<String, StageLaunchStats> getLaunches() {
        return launches;
    }
}
