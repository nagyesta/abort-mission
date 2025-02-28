package com.github.nagyesta.abortmission.core.telemetry.stats;

import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import com.github.nagyesta.abortmission.core.telemetry.converter.ClassTelemetryConverter;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Contains measurement data and statistics related to a single test class.
 */
public final class ClassTelemetry {

    private final String className;
    private final StageLaunchStats countdown;
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
        final var byMethods = converter.partitionByMethods(measurements);
        this.countdown = converter.processCountdownStats(matcherNames, byMethods);
        this.launches = converter.processLaunchStats(matcherNames, byMethods);
    }

    public String getClassName() {
        return className;
    }

    public StageLaunchStats getCountdown() {
        return countdown;
    }

    public Map<String, StageLaunchStats> getLaunches() {
        return launches;
    }
}
