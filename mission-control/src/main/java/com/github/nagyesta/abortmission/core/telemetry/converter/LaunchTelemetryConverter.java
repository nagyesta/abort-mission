package com.github.nagyesta.abortmission.core.telemetry.converter;

import com.github.nagyesta.abortmission.core.AbortMissionCommandOps;
import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;
import com.github.nagyesta.abortmission.core.healthcheck.ReadOnlyStageStatistics;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import com.github.nagyesta.abortmission.core.telemetry.stats.ClassTelemetry;

import java.util.*;
import java.util.stream.Stream;

/**
 * Converter implementation that allows simplified processing of time series data on a test suite level.
 */
public class LaunchTelemetryConverter extends BaseLaunchTelemetryConverter {

    /**
     * Creates new instance and sets default classConverter implementation.
     */
    public LaunchTelemetryConverter() {
        this(new ClassTelemetryConverter());
    }

    /**
     * Creates a new instance using the provided classConverter.
     *
     * @param classConverter The class converter we will use for class level parsing.
     */
    public LaunchTelemetryConverter(final ClassTelemetryConverter classConverter) {
        super(classConverter);
    }

    /**
     * Partitions the data per test class and calculates class level statistics.
     *
     * @param nameSpaces The namespaces we have used during test execution.
     * @return The per class statistical data.
     */
    public SortedMap<String, ClassTelemetry> processClassStatistics(final Map<String, AbortMissionCommandOps> nameSpaces) {
        Objects.requireNonNull(nameSpaces, "Namespace map cannot be null.");
        final Map<String, Map<String, Set<String>>> matchersByClassAndMethod = new TreeMap<>();
        final Map<String, List<StageTimeMeasurement>> byTestClassAccumulator = new TreeMap<>();
        missionHealthCheckEvaluatorStream(nameSpaces).forEach(evaluator -> {
            mergeInto(matchersByClassAndMethod, evaluator);
            mergeInto(byTestClassAccumulator, evaluator.getCountdownStatistics(), evaluator.getMissionStatistics());
        });
        return repartitionByClasses(matchersByClassAndMethod, byTestClassAccumulator);
    }

    /**
     * Extracts the combined stream of evaluators from the provided {@link AbortMissionCommandOps} map.
     *
     * @param nameSpaces The input map.
     * @return all evaluators
     */
    protected Stream<MissionHealthCheckEvaluator> missionHealthCheckEvaluatorStream(
            final Map<String, AbortMissionCommandOps> nameSpaces) {
        return Objects.requireNonNull(nameSpaces, "Context map cannot be null.").values().stream()
                .map(AbortMissionCommandOps::allEvaluators)
                .flatMap(Collection::stream);
    }

    /**
     * Adds all matcher names from the evaluator to all the classes and methods where it matched during the execution.
     *
     * @param matchersByClassAndMethod The target map containing previous matches we knew about.
     * @param evaluator                The currently examined evaluator.
     */
    protected void mergeInto(
            final Map<String, Map<String, Set<String>>> matchersByClassAndMethod,
            final MissionHealthCheckEvaluator evaluator) {
        Objects.requireNonNull(matchersByClassAndMethod, "Target map cannot be null.");
        Objects.requireNonNull(evaluator, "Evaluator cannot be null.");
        mergeInto(matchersByClassAndMethod,
                evaluator.getCountdownStatistics().timeSeriesStream(),
                evaluator.getMissionStatistics().timeSeriesStream(),
                addOverrideKeywordIfPresent(evaluator.overrideKeyword(), evaluator.getMatcher().getName()));
    }

    /**
     * Adds all matcher names from the evaluator to all the classes and methods where it matched during the execution.
     *
     * @param measurementsByClassName The target map containing time series data per class.
     * @param countdownStatistics     The currently examined countdown statistics.
     * @param missionStatistics       The currently examined mission statistics.
     */
    protected void mergeInto(
            final Map<String, List<StageTimeMeasurement>> measurementsByClassName,
            final ReadOnlyStageStatistics countdownStatistics,
            final ReadOnlyStageStatistics missionStatistics) {
        Objects.requireNonNull(measurementsByClassName, "Target map cannot be null.");
        Objects.requireNonNull(countdownStatistics, "CountdownStatistics cannot be null.");
        Objects.requireNonNull(missionStatistics, "MissionStatistics cannot be null.");
        mergeInto(measurementsByClassName, countdownStatistics.timeSeriesStream(), missionStatistics.timeSeriesStream());
    }

    private String addOverrideKeywordIfPresent(
            final String overrideKeyword,
            final String name) {
        return Optional.ofNullable(overrideKeyword)
                .map(kw -> "[" + kw + "] " + name)
                .orElse(name);
    }

}
