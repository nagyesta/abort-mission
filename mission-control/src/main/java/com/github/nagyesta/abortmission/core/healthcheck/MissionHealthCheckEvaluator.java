package com.github.nagyesta.abortmission.core.healthcheck;

import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;

/**
 * Defines how mission health should be measured and evaluated in the context of the matcher.
 */
public interface MissionHealthCheckEvaluator {

    /**
     * The {@link MissionHealthCheckMatcher} that will be used for finding this evaluator based on the tested component.
     *
     * @return the matcher that will identify this evaluator during tests.
     */
    MissionHealthCheckMatcher getMatcher();

    /**
     * The read-only view of the statistics we are collecting during the test runs.
     *
     * @return the statistics we are collecting.
     */
    MissionStatisticsView getStats();

    /**
     * Returns the number of times countdown needs to start/complete for collecting an initial baseline for test failures.
     *
     * @return the number of baseline runs.
     */
    int getBurnInTestCount();

    /**
     * See {@link MissionStatisticsView#getCountdownStarted()}.
     *
     * @return count;
     */
    int getCountdownStartCount();

    /**
     * See {@link MissionStatisticsView#getCountdownAborted()}.
     *
     * @return count;
     */
    int getCountdownAbortCount();

    /**
     * See {@link MissionStatisticsView#getCountdownCompleted()}.
     *
     * @return count;
     */
    int getCountdownCompleteCount();

    /**
     * See {@link MissionStatisticsView#getMissionSuccess()}.
     *
     * @return count;
     */
    int getMissionSuccessCount();

    /**
     * See {@link MissionStatisticsView#getMissionFailure()}.
     *
     * @return count;
     */
    int getMissionFailureCount();

    /**
     * See {@link MissionStatisticsView#getMissionAbort()}.
     *
     * @return count;
     */
    int getMissionAbortCount();

    /**
     * Tells the launch controller whether or not we should abort the launch after the preparation was done.
     *
     * @return true if we are after burn-in and the number of failures is more than the threshold, false otherwise.
     */
    boolean shouldAbort();

    /**
     * Tells the launch controller whether or not we should abort the launch before starting the preparation
     * steps (as they are assumed to be failing already).
     *
     * @return true if we are after burn-in and the countdown never completed, false otherwise.
     */
    boolean shouldAbortCountdown();

    /**
     * Increases the number counting test preparation started.
     */
    void logCountdownStarted();

    /**
     * Increases the number counting test preparation was aborted.
     */
    void logCountdownAborted();

    /**
     * Increases the number counting test preparation completed.
     */
    void logLaunchImminent();

    /**
     * Increases the number counting failures (during or after the test preparation steps).
     */
    void logMissionFailure();

    /**
     * Increases the number counting test runs aborted.
     */
    void logMissionAbort();

    /**
     * Increases the number counting test run successes.
     */
    void logMissionSuccess();

    /**
     * Returns the predefined message that belongs ot the health check (or the matcher if not set).
     *
     * @return The message that can be used by summary generators.
     */
    String getMessage();
}
