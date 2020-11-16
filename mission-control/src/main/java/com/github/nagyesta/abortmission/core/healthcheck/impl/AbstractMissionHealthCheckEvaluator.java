package com.github.nagyesta.abortmission.core.healthcheck.impl;

import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;
import com.github.nagyesta.abortmission.core.healthcheck.ReadOnlyMissionStatistics;
import com.github.nagyesta.abortmission.core.healthcheck.ReadOnlyStageStatistics;
import com.github.nagyesta.abortmission.core.healthcheck.StatisticsLogger;
import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;

/**
 * Implements the common functionality of {@link MissionHealthCheckEvaluator} instances.
 */
public abstract class AbstractMissionHealthCheckEvaluator implements MissionHealthCheckEvaluator {

    private final MissionHealthCheckMatcher matcher;
    private final MissionStatisticsCollector stats;

    /**
     * Sets the matcher and the mission statistics collector.
     *
     * @param matcher The health check matcher mentioned by {@link MissionHealthCheckEvaluator#getMatcher()}.
     * @param stats   The statistics collector mentioned by {@link MissionHealthCheckEvaluator#getStats()}.
     */
    protected AbstractMissionHealthCheckEvaluator(final MissionHealthCheckMatcher matcher, final MissionStatisticsCollector stats) {
        this.matcher = matcher;
        this.stats = stats;
    }

    @Override
    public ReadOnlyMissionStatistics getStats() {
        return this.stats;
    }

    @Override
    public MissionHealthCheckMatcher getMatcher() {
        return matcher;
    }

    @Override
    public ReadOnlyStageStatistics getCountdownStatistics() {
        return stats.getReadOnlyCountdown();
    }

    @Override
    public StatisticsLogger countdownLogger() {
        return stats.getCountdown();
    }

    @Override
    public ReadOnlyStageStatistics getMissionStatistics() {
        return stats.getReadOnlyMission();
    }

    @Override
    public StatisticsLogger missionLogger() {
        return stats.getMission();
    }
}
