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
    ReadOnlyMissionStatistics getStats();

    /**
     * Returns the number of times countdown needs to start/complete for collecting an initial baseline for test failures.
     *
     * @return the number of baseline runs.
     */
    int getBurnInTestCount();

    /**
     * Returns the read-only view of the countdown statistics.
     *
     * @return countdown;
     */
    ReadOnlyStageStatistics getCountdownStatistics();

    /**
     * Returns the read-only view of the mission statistics.
     *
     * @return mission;
     */
    ReadOnlyStageStatistics getMissionStatistics();

    /**
     * Tells us whether this evaluator should never abort and suppress other matching evaluators if they would.
     *
     * @return true if the evaluator should suppress abort, false otherwise.
     */
    boolean shouldSuppressAbort();

    /**
     * Tells us whether this evaluator should always abort.
     * <b>Important:</b> If two different evaluators are matching and one returns true for {@link #shouldSuppressAbort()} while
     * the other returns true for {@code shouldForceAbort()}, then the "suppression" will take precedence and neither will abort.
     *
     * @return true if the evaluator should always abort.
     */
    boolean shouldForceAbort();

    /**
     * Tells the launch controller whether we should abort the launch after the preparation was done.
     *
     * @return true if we are after burn-in and the number of failures is more than the threshold, false otherwise.
     */
    boolean shouldAbort();

    /**
     * Tells the launch controller whether we should abort the launch before starting the preparation
     * steps (as they are assumed to be failing already).
     *
     * @return true if we are after burn-in and the countdown never completed, false otherwise.
     */
    boolean shouldAbortCountdown();

    /**
     * Returns the countdown specific statistics logger we are using.
     *
     * @return countdown
     */
    StatisticsLogger countdownLogger();

    /**
     * Returns the mission specific statistics logger we are using.
     *
     * @return mission
     */
    StatisticsLogger missionLogger();

    /**
     * Returns the keyword we can use for overriding abort/disarm decisions for the evaluator using command line parameters.
     *
     * @return override keyword
     */
    String overrideKeyword();
}
