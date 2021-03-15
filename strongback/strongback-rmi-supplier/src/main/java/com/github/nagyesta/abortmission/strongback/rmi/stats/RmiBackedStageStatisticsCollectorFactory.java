package com.github.nagyesta.abortmission.strongback.rmi.stats;

import com.github.nagyesta.abortmission.core.healthcheck.StageStatistics;
import com.github.nagyesta.abortmission.core.healthcheck.StageStatisticsCollectorFactory;
import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;

import java.rmi.registry.Registry;

/**
 * Factory for creating {@link RmiBackedStageStatisticsCollector} instances.
 */
public final class RmiBackedStageStatisticsCollectorFactory implements StageStatisticsCollectorFactory {

    private final String contextName;
    private final Registry registry;

    /**
     * Creates an instance and sets the contextName and dataSource.
     *
     * @param contextName The name of the Abort-Mission context.
     * @param registry    The registry we use for accessing the service.
     */
    public RmiBackedStageStatisticsCollectorFactory(final String contextName, final Registry registry) {
        this.contextName = contextName;
        this.registry = registry;
    }

    @Override
    public StageStatistics newCountdownStatistics(final MissionHealthCheckMatcher matcher) {
        return new RmiBackedStageStatisticsCollector(contextName, matcher, registry, true);
    }

    @Override
    public StageStatistics newMissionStatistics(final MissionHealthCheckMatcher matcher) {
        return new RmiBackedStageStatisticsCollector(contextName, matcher, registry, false);
    }
}
