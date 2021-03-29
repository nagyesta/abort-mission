package com.github.nagyesta.abortmission.strongback.rmi.stats;

import com.github.nagyesta.abortmission.core.healthcheck.StageStatisticsSnapshot;
import com.github.nagyesta.abortmission.core.healthcheck.impl.DefaultStageStatisticsSnapshot;
import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import com.github.nagyesta.abortmission.strongback.base.ExternalStageStatisticsCollector;
import com.github.nagyesta.abortmission.strongback.base.StrongbackException;
import com.github.nagyesta.abortmission.strongback.rmi.server.RmiServiceProvider;
import com.github.nagyesta.abortmission.strongback.rmi.service.LaunchStatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * RMI backed implementation of {@link com.github.nagyesta.abortmission.core.healthcheck.impl.StageStatisticsCollector}.
 */
public class RmiBackedStageStatisticsCollector extends ExternalStageStatisticsCollector {

    private static final Logger LOGGER = LoggerFactory.getLogger(RmiBackedStageStatisticsCollector.class);
    private static final StageStatisticsSnapshot DEFAULT_SNAPSHOT =
            new DefaultStageStatisticsSnapshot(0, 0, 0, 0);

    private final Registry registry;

    /**
     * Simple constructor only setting the matcher.
     *
     * @param contextName The name of the Abort-Mission context.
     * @param matcher     The matcher held by the evaluator owning this collector as well.
     * @param registry    The Registry used for obtaining the RMI connection.
     * @param countdown   True if the collector will be used for countdown.
     */
    public RmiBackedStageStatisticsCollector(final String contextName,
                                             final MissionHealthCheckMatcher matcher,
                                             final Registry registry,
                                             final boolean countdown) {
        super(contextName, matcher, countdown);
        this.registry = registry;
    }

    @Override
    protected StageStatisticsSnapshot doGetSnapshot(final String contextName,
                                                    final MissionHealthCheckMatcher matcher,
                                                    final boolean countdown) {
        try {
            final LaunchStatisticsService service = RmiServiceProvider.service(registry);
            return Optional.ofNullable(service.getSnapshot(contextName, matcher.getName(), countdown))
                    .map(RmiStageStatisticsSnapshot::toStageStatisticsSnapshot)
                    .orElse(DEFAULT_SNAPSHOT);
        } catch (final RemoteException e) {
            LOGGER.error("Failed to obtain snapshot.", e);
            throw new StrongbackException(e.getMessage(), e);
        }
    }

    @Override
    protected List<StageTimeMeasurement> doFetchAll(final String contextName,
                                                    final MissionHealthCheckMatcher matcher,
                                                    final boolean countdown) {
        try {
            final LaunchStatisticsService service = RmiServiceProvider.service(registry);
            return service.fetchMeasurementsFor(contextName, matcher.getName(), countdown).stream()
                    .map(RmiStageTimeMeasurement::toStageTimeMeasurement)
                    .collect(Collectors.toList());
        } catch (final RemoteException e) {
            LOGGER.error("Failed to fetch all.", e);
            throw new StrongbackException(e.getMessage(), e);
        }
    }

    @Override
    protected void doLogTimeMeasurement(final String contextName,
                                        final MissionHealthCheckMatcher matcher,
                                        final boolean countdown,
                                        final StageTimeMeasurement measurement) {
        try {
            final LaunchStatisticsService service = RmiServiceProvider.service(registry);
            final RmiStageTimeMeasurement rmiMeasurement = new RmiStageTimeMeasurement(measurement);
            service.insertStageTimeMeasurement(contextName, matcher.getName(), countdown, rmiMeasurement);
        } catch (final RemoteException e) {
            LOGGER.error("Failed to log measurement.", e);
            throw new StrongbackException(e.getMessage(), e);
        }
    }

}
