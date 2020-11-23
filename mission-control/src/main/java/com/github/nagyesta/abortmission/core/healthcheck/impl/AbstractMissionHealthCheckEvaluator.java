package com.github.nagyesta.abortmission.core.healthcheck.impl;

import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;
import com.github.nagyesta.abortmission.core.healthcheck.ReadOnlyMissionStatistics;
import com.github.nagyesta.abortmission.core.healthcheck.ReadOnlyStageStatistics;
import com.github.nagyesta.abortmission.core.healthcheck.StatisticsLogger;
import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;

import static com.github.nagyesta.abortmission.core.MissionControl.ABORT_MISSION_DISARM_COUNTDOWN;
import static com.github.nagyesta.abortmission.core.MissionControl.ABORT_MISSION_DISARM_MISSION;

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

    @Override
    public boolean shouldAbort() {
        if (isDisarmed(ABORT_MISSION_DISARM_MISSION)) {
            return false;
        }
        return shouldAbortInternal();
    }

    @Override
    public boolean shouldAbortCountdown() {
        if (isDisarmed(ABORT_MISSION_DISARM_COUNTDOWN)) {
            return false;
        }
        return shouldAbortCountdownInternal();
    }

    /**
     * Determines whether the disarm switches are set or not.
     *
     * @param switchName The property name of the switch.
     * @return true is the switch is true (a.k.a disarmed) false otherwise
     */
    protected boolean isDisarmed(final String switchName) {
        return Boolean.TRUE.toString().equalsIgnoreCase(System.getProperty(switchName));
    }

    /**
     * Decides whether the current implementation needs to abort the mission.
     *
     * @return true if the implementing class decides to abort the mission, false otherwise
     */
    protected abstract boolean shouldAbortInternal();

    /**
     * Decides whether the current implementation needs to abort the countdown.
     *
     * @return true if the implementing class decides to abort the countdown, false otherwise
     */
    protected abstract boolean shouldAbortCountdownInternal();
}
