package com.github.nagyesta.abortmission.strongback.h2.stats;

import com.github.nagyesta.abortmission.core.healthcheck.StageStatisticsSnapshot;
import com.github.nagyesta.abortmission.core.healthcheck.impl.DefaultStageStatisticsSnapshot;
import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import com.github.nagyesta.abortmission.strongback.base.ExternalStageStatisticsCollector;
import com.github.nagyesta.abortmission.strongback.h2.repository.LaunchStatisticsRepository;
import com.github.nagyesta.abortmission.strongback.h2.server.H2DataSourceProvider;
import org.jdbi.v3.core.Jdbi;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

/**
 * H2 DB backed implementation of {@link com.github.nagyesta.abortmission.core.healthcheck.impl.StageStatisticsCollector}.
 */
public class H2BackedStageStatisticsCollector extends ExternalStageStatisticsCollector {

    private static final StageStatisticsSnapshot DEFAULT_SNAPSHOT =
            new DefaultStageStatisticsSnapshot(0, 0, 0, 0);

    private final Jdbi jdbi;

    /**
     * Simple constructor only setting the matcher.
     *
     * @param contextName The name of the Abort-Mission context.
     * @param matcher     The matcher held by the evaluator owning this collector as well.
     * @param dataSource  The dataSource used for obtaining the DB connection.
     * @param countdown   True if the collector will be used for countdown.
     */
    public H2BackedStageStatisticsCollector(final String contextName,
                                            final MissionHealthCheckMatcher matcher,
                                            final DataSource dataSource,
                                            final boolean countdown) {
        super(contextName, matcher, countdown);
        this.jdbi = H2DataSourceProvider.jdbi(dataSource);
    }

    @Override
    protected StageStatisticsSnapshot doGetSnapshot(final String contextName,
                                                    final MissionHealthCheckMatcher matcher,
                                                    final boolean countdown) {
        return Optional.ofNullable(jdbi.withExtension(LaunchStatisticsRepository.class,
                dao -> dao.getSnapshot(contextName, matcher.getName(), countdown)))
                .orElse(DEFAULT_SNAPSHOT);
    }

    @Override
    protected List<StageTimeMeasurement> doFetchAll(final String contextName,
                                                    final MissionHealthCheckMatcher matcher,
                                                    final boolean countdown) {
        return jdbi.withExtension(LaunchStatisticsRepository.class,
                dao -> dao.fetchMeasurementsFor(contextName, matcher.getName(), countdown));
    }

    @Override
    protected void doLogTimeMeasurement(final String contextName,
                                        final MissionHealthCheckMatcher matcher,
                                        final boolean countdown,
                                        final StageTimeMeasurement measurement) {
        jdbi.withExtension(LaunchStatisticsRepository.class, dao -> {
            dao.insertStageTimeMeasurement(contextName, matcher.getName(), countdown, measurement);
            return 0;
        });
    }

}
