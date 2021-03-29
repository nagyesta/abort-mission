package com.github.nagyesta.abortmission.core.healthcheck;

import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;

/**
 * Defines how counting mission success/failure events should work.
 */
public interface StageStatistics extends ReadOnlyStageStatistics, StatisticsLogger {

    /**
     * Returns the matcher this statistics logger belongs to.
     *
     * @return the matcher
     */
    MissionHealthCheckMatcher getMatcher();

}
