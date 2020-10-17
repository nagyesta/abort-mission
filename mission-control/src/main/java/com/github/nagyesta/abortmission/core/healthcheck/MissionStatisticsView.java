package com.github.nagyesta.abortmission.core.healthcheck;

/**
 * Read-only view of mission health statistics.
 */
public interface MissionStatisticsView {
    /**
     * Returns the number of times countdown start was logged.
     *
     * @return count
     */
    int getCountdownStarted();

    /**
     * Returns the number of times countdown was completed successfully.
     *
     * @return count
     */
    int getCountdownCompleted();

    /**
     * Returns the number of times countdown was aborted.
     *
     * @return count
     */
    int getCountdownAborted();

    /**
     * Returns the number of times the mission was successful (after the countdown was finished).
     *
     * @return count
     */
    int getMissionSuccess();

    /**
     * Returns the number of times the mission was failed (after the countdown was finished).
     *
     * @return count
     */
    int getMissionFailure();

    /**
     * Returns the number of times the mission was aborted (after the countdown was finished).
     *
     * @return count
     */
    int getMissionAbort();
}
