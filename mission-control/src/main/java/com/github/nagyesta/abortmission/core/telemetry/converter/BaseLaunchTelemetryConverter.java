package com.github.nagyesta.abortmission.core.telemetry.converter;

import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import com.github.nagyesta.abortmission.core.telemetry.stats.ClassTelemetry;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Base parts of a launch telemetry converter, handling the common parts of the conversion.
 */
public class BaseLaunchTelemetryConverter {

    private final ClassTelemetryConverter classConverter;

    /**
     * Creates a new instance and sets the class converter.
     *
     * @param classConverter The class converter.
     */
    public BaseLaunchTelemetryConverter(final ClassTelemetryConverter classConverter) {
        this.classConverter = Objects.requireNonNull(classConverter, "ClassConverter cannot be null.");
    }

    /**
     * Creates new partitions based on thy matcher and measurement maps.
     *
     * @param matchersByClassAndMethod The Map containing the Set of matcher names for each method of each class.
     * @param byTestClassAccumulator   The Map containing the time series data per ech class.
     * @return The repartitioned data in {@link ClassTelemetry} format.
     */
    protected SortedMap<String, ClassTelemetry> repartitionByClasses(final Map<String, Map<String, Set<String>>> matchersByClassAndMethod,
                                                                     final Map<String, List<StageTimeMeasurement>> byTestClassAccumulator) {
        final SortedMap<String, ClassTelemetry> parsedClasses = new TreeMap<>();
        byTestClassAccumulator.forEach((className, measurementList) -> {
            final var matcherNames = matchersByClassAndMethod.getOrDefault(className, Collections.emptyMap());
            parsedClasses.put(className, new ClassTelemetry(classConverter, className, measurementList, matcherNames));
        });
        return Collections.unmodifiableSortedMap(parsedClasses);
    }

    /**
     * Adds all matcher name from the evaluator to all the classes and methods where it matched during the execution.
     *
     * @param matchersByClassAndMethod The names of all matchers applicable for the particular classes and methods.
     * @param countdownTimeSeries      The currently examined countdown statistics as a time series stream.
     * @param missionTimeSeries        The currently examined mission statistics as a time series stream.
     * @param matcherName              The name of the matcher which was used for collecting the time series data.
     */
    protected void mergeInto(final Map<String, Map<String, Set<String>>> matchersByClassAndMethod,
                             final Stream<StageTimeMeasurement> countdownTimeSeries,
                             final Stream<StageTimeMeasurement> missionTimeSeries,
                             final String matcherName) {
        Objects.requireNonNull(matchersByClassAndMethod, "Target map cannot be null.");
        Objects.requireNonNull(countdownTimeSeries, "CountdownTimeSeries cannot be null.");
        Objects.requireNonNull(missionTimeSeries, "MissionTimeSeries cannot be null.");
        Objects.requireNonNull(matcherName, "MatcherName cannot be null.");
        mergeMatcherNamesByClassAndMethodName(matchersByClassAndMethod, countdownTimeSeries, matcherName);
        mergeMatcherNamesByClassAndMethodName(matchersByClassAndMethod, missionTimeSeries, matcherName);
    }

    /**
     * Adds all matcher name from the evaluator to all the classes and methods where it matched during the execution.
     *
     * @param measurementsByClassName The target map containing time series data per class.
     * @param countdownTimeSeries     The currently examined countdown statistics as a time series stream.
     * @param missionTimeSeries       The currently examined mission statistics as a time series stream.
     */
    protected void mergeInto(final Map<String, List<StageTimeMeasurement>> measurementsByClassName,
                             final Stream<StageTimeMeasurement> countdownTimeSeries,
                             final Stream<StageTimeMeasurement> missionTimeSeries) {
        Objects.requireNonNull(measurementsByClassName, "Target map cannot be null.");
        Objects.requireNonNull(countdownTimeSeries, "CountdownTimeSeries cannot be null.");
        Objects.requireNonNull(missionTimeSeries, "MissionTimeSeries cannot be null.");
        partitionTimeSeriesByClassName(measurementsByClassName, countdownTimeSeries);
        partitionTimeSeriesByClassName(measurementsByClassName, missionTimeSeries);
    }

    private void partitionTimeSeriesByClassName(
            final Map<String, List<StageTimeMeasurement>> targetMap,
            final Stream<StageTimeMeasurement> timeSeriesStream) {
        timeSeriesStream.collect(Collectors.groupingBy(StageTimeMeasurement::getTestClassId))
                .forEach((testClassId, measurementList) -> measurementList.stream()
                        .collect(Collectors.groupingBy(StageTimeMeasurement::getTestCaseId))
                        .forEach((testCaseId, measurements) ->
                                mergeIntoIntermediateClassMap(targetMap, testClassId, measurementList)));
    }

    private void mergeMatcherNamesByClassAndMethodName(
            final Map<String, Map<String, Set<String>>> targetMap,
            final Stream<StageTimeMeasurement> timeSeriesStream,
            final String evaluator) {
        timeSeriesStream.collect(Collectors.groupingBy(StageTimeMeasurement::getTestClassId))
                .forEach((testClassId, measurementList) -> measurementList.stream()
                        .collect(Collectors.groupingBy(StageTimeMeasurement::getTestCaseId))
                        .forEach((testCaseId, measurements) -> targetMap
                                .computeIfAbsent(testClassId, key -> new TreeMap<>())
                                .computeIfAbsent(testCaseId, key -> new TreeSet<>())
                                .add(evaluator)));
    }

    private void mergeIntoIntermediateClassMap(
            final Map<String, List<StageTimeMeasurement>> targetMap,
            final String testClassId,
            final List<StageTimeMeasurement> measurementList) {
        targetMap.computeIfAbsent(testClassId, key -> new ArrayList<>())
                .addAll(measurementList);
    }
}
