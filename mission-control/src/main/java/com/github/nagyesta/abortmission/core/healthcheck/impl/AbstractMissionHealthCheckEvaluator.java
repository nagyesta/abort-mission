package com.github.nagyesta.abortmission.core.healthcheck.impl;

import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;
import com.github.nagyesta.abortmission.core.healthcheck.MissionStatisticsView;
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
    public MissionStatisticsView getStats() {
        return this.stats;
    }

    @Override
    public MissionHealthCheckMatcher getMatcher() {
        return matcher;
    }

    @Override
    public int getMissionSuccessCount() {
        return stats.getMissionSuccess();
    }

    @Override
    public int getMissionFailureCount() {
        return stats.getMissionFailure();
    }

    @Override
    public int getMissionAbortCount() {
        return stats.getMissionAbort();
    }

    @Override
    public int getCountdownStartCount() {
        return stats.getCountdownStarted();
    }

    @Override
    public int getCountdownAbortCount() {
        return stats.getCountdownAborted();
    }

    @Override
    public int getCountdownCompleteCount() {
        return stats.getCountdownCompleted();
    }

    @Override
    public void logCountdownStarted() {
        stats.incrementCountdownStarted();
    }

    @Override
    public void logLaunchImminent() {
        stats.incrementCountdownCompleted();
    }

    @Override
    public void logMissionFailure() {
        stats.incrementMissionFailure();
    }

    @Override
    public void logMissionAbort() {
        stats.incrementMissionAbort();
    }

    @Override
    public void logCountdownAborted() {
        stats.incrementCountdownAborted();
    }

    @Override
    public void logMissionSuccess() {
        stats.incrementMissionSuccess();
    }

}
