package com.github.nagyesta.abortmission.core.healthcheck.impl;

import com.github.nagyesta.abortmission.core.healthcheck.StageStatistics;
import com.github.nagyesta.abortmission.core.healthcheck.StageStatisticsCollectorFactory;
import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;

/**
 * Default implementation of {@link StageStatisticsCollectorFactory}.
 */
public class DefaultStageStatisticsCollectorFactory implements StageStatisticsCollectorFactory {

    @Override
    public StageStatistics newCountdownStatistics(final MissionHealthCheckMatcher matcher) {
        return new StageStatisticsCollector(matcher);
    }

    @Override
    public StageStatistics newMissionStatistics(final MissionHealthCheckMatcher matcher) {
        return new StageStatisticsCollector(matcher);
    }
}
