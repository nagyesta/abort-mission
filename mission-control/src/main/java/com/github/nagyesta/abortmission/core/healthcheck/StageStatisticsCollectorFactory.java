package com.github.nagyesta.abortmission.core.healthcheck;

import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;

/**
 * Factory for creating {@link StageStatistics} instances.
 */
public interface StageStatisticsCollectorFactory {

    /**
     * Creates a countdown statistics connector.
     *
     * @param matcher The matcher which will be used by the evaluator calling the collector.
     * @return statsCollector
     */
    StageStatistics newCountdownStatistics(MissionHealthCheckMatcher matcher);

    /**
     * Creates a mission statistics connector.
     *
     * @param matcher The matcher which will be used by the evaluator calling the collector.
     * @return statsCollector
     */
    StageStatistics newMissionStatistics(MissionHealthCheckMatcher matcher);
}
