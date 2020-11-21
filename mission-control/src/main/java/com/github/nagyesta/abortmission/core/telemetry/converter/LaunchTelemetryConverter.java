package com.github.nagyesta.abortmission.core.telemetry.converter;

import com.github.nagyesta.abortmission.core.AbortMissionCommandOps;
import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;
import com.github.nagyesta.abortmission.core.healthcheck.ReadOnlyStageStatistics;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import com.github.nagyesta.abortmission.core.telemetry.stats.ClassTelemetry;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Converter implementation that allows simplified processing of time series data on a test suite level.
 */
public class LaunchTelemetryConverter {

    private final ClassTelemetryConverter classConverter;

    /**
     * Creates new instance and sets default classConverter implementation.
     */
    public LaunchTelemetryConverter() {
        this(new ClassTelemetryConverter());
    }

    /**
     * Creates new instance using the provided classConverter.
     *
     * @param classConverter The class converter we will use for class level parsing.
     */
    public LaunchTelemetryConverter(final ClassTelemetryConverter classConverter) {
        this.classConverter = Objects.requireNonNull(classConverter, "ClassConverter cannot be null.");
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
        final SortedMap<String, ClassTelemetry> parsedClasses = new TreeMap<>();
        byTestClassAccumulator.forEach((className, measurementList) -> {
            final Map<String, Set<String>> matcherNames = matchersByClassAndMethod.getOrDefault(className, Collections.emptyMap());
            parsedClasses.put(className, new ClassTelemetry(classConverter, className, measurementList, matcherNames));
        });
        return Collections.unmodifiableSortedMap(parsedClasses);
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
     * Adds all matcher name from the evaluator to all of the classes and methods where it matched during the execution.
     *
     * @param matchersByClassAndMethod The target map containing previous matches we knew about.
     * @param evaluator                The currently examined evaluator.
     */
    protected void mergeInto(final Map<String, Map<String, Set<String>>> matchersByClassAndMethod,
                             final MissionHealthCheckEvaluator evaluator) {
        Objects.requireNonNull(matchersByClassAndMethod, "Target map cannot be null.");
        Objects.requireNonNull(evaluator, "Evaluator cannot be null.");
        mergeMatcherNamesByClassAndMethodName(matchersByClassAndMethod,
                evaluator.getCountdownStatistics().timeSeriesStream(), evaluator.getMatcher().getName());
        mergeMatcherNamesByClassAndMethodName(matchersByClassAndMethod,
                evaluator.getMissionStatistics().timeSeriesStream(), evaluator.getMatcher().getName());
    }

    /**
     * Adds all matcher name from the evaluator to all of the classes and methods where it matched during the execution.
     *
     * @param measurementsByClassName The target map containing time series data per class.
     * @param countdownStatistics     The currently examined countdown statistics.
     * @param missionStatistics       The currently examined mission statistics.
     */
    protected void mergeInto(final Map<String, List<StageTimeMeasurement>> measurementsByClassName,
                             final ReadOnlyStageStatistics countdownStatistics,
                             final ReadOnlyStageStatistics missionStatistics) {
        Objects.requireNonNull(measurementsByClassName, "Target map cannot be null.");
        Objects.requireNonNull(countdownStatistics, "CountdownStatistics cannot be null.");
        Objects.requireNonNull(missionStatistics, "MissionStatistics cannot be null.");
        partitionTimeSeriesByClassName(measurementsByClassName, countdownStatistics.timeSeriesStream());
        partitionTimeSeriesByClassName(measurementsByClassName, missionStatistics.timeSeriesStream());
    }

    private void partitionTimeSeriesByClassName(
            final Map<String, List<StageTimeMeasurement>> targetMap,
            final Stream<StageTimeMeasurement> timeSeriesStream) {
        timeSeriesStream.collect(Collectors.groupingBy(StageTimeMeasurement::getTestClassId))
                .forEach((testClassId, measurementList) -> {
                    measurementList.stream()
                            .collect(Collectors.groupingBy(StageTimeMeasurement::getTestCaseId))
                            .forEach((testCaseId, measurements) -> {
                                mergeIntoIntermediateClassMap(targetMap, testClassId, measurementList);
                            });
                });
    }

    private void mergeMatcherNamesByClassAndMethodName(
            final Map<String, Map<String, Set<String>>> targetMap,
            final Stream<StageTimeMeasurement> timeSeriesStream,
            final String evaluator) {
        timeSeriesStream.collect(Collectors.groupingBy(StageTimeMeasurement::getTestClassId))
                .forEach((testClassId, measurementList) -> {
                    measurementList.stream()
                            .collect(Collectors.groupingBy(StageTimeMeasurement::getTestCaseId))
                            .forEach((testCaseId, measurements) -> {
                                targetMap.computeIfAbsent(testClassId, key -> new TreeMap<>())
                                        .computeIfAbsent(testCaseId, key -> new TreeSet<>())
                                        .add(evaluator);
                            });
                });
    }

    private void mergeIntoIntermediateClassMap(
            final Map<String, List<StageTimeMeasurement>> targetMap,
            final String testClassId,
            final List<StageTimeMeasurement> measurementList) {
        targetMap.computeIfAbsent(testClassId, key -> new ArrayList<>())
                .addAll(measurementList);
    }

}
