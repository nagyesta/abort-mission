package com.github.nagyesta.abortmission.strongback.h2.stats;

import com.github.nagyesta.abortmission.core.healthcheck.StageStatistics;
import com.github.nagyesta.abortmission.core.healthcheck.StageStatisticsCollectorFactory;
import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;

import javax.sql.DataSource;

/**
 * Factory for creating {@link H2BackedStageStatisticsCollector} instances.
 */
public final class H2BackedStageStatisticsCollectorFactory implements StageStatisticsCollectorFactory {

    private final String contextName;
    private final DataSource dataSource;

    /**
     * Creates an instance and sets the contextName and dataSource.
     *
     * @param contextName The name of the Abort-Mission context.
     * @param dataSource  The data source for accessing the DB.
     */
    public H2BackedStageStatisticsCollectorFactory(final String contextName, final DataSource dataSource) {
        this.contextName = contextName;
        this.dataSource = dataSource;
    }

    @Override
    public StageStatistics newCountdownStatistics(final MissionHealthCheckMatcher matcher) {
        return new H2BackedStageStatisticsCollector(contextName, matcher, dataSource, true);
    }

    @Override
    public StageStatistics newMissionStatistics(final MissionHealthCheckMatcher matcher) {
        return new H2BackedStageStatisticsCollector(contextName, matcher, dataSource, false);
    }
}
