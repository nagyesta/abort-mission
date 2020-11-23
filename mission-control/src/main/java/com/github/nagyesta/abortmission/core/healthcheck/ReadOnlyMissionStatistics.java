package com.github.nagyesta.abortmission.core.healthcheck;

/**
 * Read-only view of mission health statistics.
 */
public interface ReadOnlyMissionStatistics {

    /**
     * Returns the countdown specific statistics.
     *
     * @return countdown.
     */
    ReadOnlyStageStatistics getReadOnlyCountdown();

    /**
     * Returns the mission specific statistics.
     *
     * @return mission.
     */
    ReadOnlyStageStatistics getReadOnlyMission();
}
