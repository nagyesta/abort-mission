package com.github.nagyesta.abortmission.strongback.rmi.service.impl;

import com.github.nagyesta.abortmission.core.telemetry.StageResult;
import com.github.nagyesta.abortmission.strongback.rmi.service.LaunchStatisticsService;
import com.github.nagyesta.abortmission.strongback.rmi.stats.RmiStageStatisticsSnapshot;
import com.github.nagyesta.abortmission.strongback.rmi.stats.RmiStageTimeMeasurement;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Map based implementation of {@link LaunchStatisticsService}.
 */
public class SingletonLaunchStatisticsService implements LaunchStatisticsService {

    private final Map<String, Map<String, Map<Boolean, List<RmiStageTimeMeasurement>>>> store = new ConcurrentHashMap<>();

    /**
     * Returns the shared service instance.
     *
     * @return the shared instance.
     */
    public static SingletonLaunchStatisticsService shared() {
        return SingletonLaunchStatisticsServiceHolder.INSTANCE;
    }

    @Override
    public void insertStageTimeMeasurement(final String contextName,
                                           final String matcherName,
                                           final boolean countdown,
                                           final RmiStageTimeMeasurement measurement) {
        store.computeIfAbsent(contextName, name -> new ConcurrentHashMap<>())
                .computeIfAbsent(matcherName, name -> new ConcurrentHashMap<>())
                .computeIfAbsent(countdown, c -> new CopyOnWriteArrayList<>())
                .add(measurement);

    }

    @Override
    public List<RmiStageTimeMeasurement> fetchAllMeasurementsForMatcher(final String matcherName) {
        return store.values().stream()
                .filter(contextMap -> contextMap.containsKey(matcherName))
                .flatMap(contextMap -> Stream.of(contextMap.get(matcherName)))
                .flatMap(matcherMap -> matcherMap.values().stream())
                .flatMap(Collection::stream)
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public List<RmiStageTimeMeasurement> fetchMeasurementsFor(final String contextName,
                                                              final String matcherName,
                                                              final boolean countdown) {
        if (contextName == null || matcherName == null) {
            return Collections.emptyList();
        }
        return store.computeIfAbsent(contextName, name -> new ConcurrentHashMap<>())
                .computeIfAbsent(matcherName, name -> new ConcurrentHashMap<>())
                .computeIfAbsent(countdown, c -> new CopyOnWriteArrayList<>())
                .stream()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public List<String> fetchAllMatcherNames() {
        return store.values().stream()
                .map(Map::keySet)
                .flatMap(Collection::stream)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public RmiStageStatisticsSnapshot getSnapshot(final String contextName,
                                                  final String matcherName,
                                                  final boolean countdown) {
        final Map<StageResult, Integer> resultMap = fetchMeasurementsFor(contextName, matcherName, countdown).stream()
                .collect(Collectors.groupingBy(RmiStageTimeMeasurement::getResult))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size()));
        return new RmiStageStatisticsSnapshot(
                resultMap.getOrDefault(StageResult.FAILURE, 0),
                resultMap.getOrDefault(StageResult.SUCCESS, 0),
                resultMap.getOrDefault(StageResult.ABORT, 0),
                resultMap.getOrDefault(StageResult.SUPPRESSED, 0));
    }

    private static class SingletonLaunchStatisticsServiceHolder {
        private static final SingletonLaunchStatisticsService INSTANCE = new SingletonLaunchStatisticsService();
    }
}
