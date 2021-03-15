package com.github.nagyesta.abortmission.strongback.rmi.service.impl;

import com.github.nagyesta.abortmission.strongback.rmi.service.LaunchStatisticsService;
import com.github.nagyesta.abortmission.strongback.rmi.stats.RmiStageStatisticsSnapshot;
import com.github.nagyesta.abortmission.strongback.rmi.stats.RmiStageTimeMeasurement;

import java.util.List;

/**
 * Map based implementation of {@link LaunchStatisticsService}.
 */
public class LaunchStatisticsServiceImpl implements LaunchStatisticsService {

    @Override
    public void insertStageTimeMeasurement(final String contextName,
                                           final String matcherName,
                                           final boolean countdown,
                                           final RmiStageTimeMeasurement measurement) {
        SingletonLaunchStatisticsService.shared()
                .insertStageTimeMeasurement(contextName, matcherName, countdown, measurement);
    }

    @Override
    public List<RmiStageTimeMeasurement> fetchAllMeasurementsForMatcher(final String matcherName) {
        return SingletonLaunchStatisticsService.shared()
                .fetchAllMeasurementsForMatcher(matcherName);
    }

    @Override
    public List<RmiStageTimeMeasurement> fetchMeasurementsFor(final String contextName,
                                                              final String matcherName,
                                                              final boolean countdown) {
        return SingletonLaunchStatisticsService.shared()
                .fetchMeasurementsFor(contextName, matcherName, countdown);
    }

    @Override
    public List<String> fetchAllMatcherNames() {
        return SingletonLaunchStatisticsService.shared()
                .fetchAllMatcherNames();
    }

    @Override
    public RmiStageStatisticsSnapshot getSnapshot(final String contextName,
                                                  final String matcherName,
                                                  final boolean countdown) {
        return SingletonLaunchStatisticsService.shared()
                .getSnapshot(contextName, matcherName, countdown);
    }
}
