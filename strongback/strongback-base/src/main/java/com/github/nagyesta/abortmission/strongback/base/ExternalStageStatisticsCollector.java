package com.github.nagyesta.abortmission.strongback.base;

import com.github.nagyesta.abortmission.core.healthcheck.StageStatistics;
import com.github.nagyesta.abortmission.core.healthcheck.StageStatisticsSnapshot;
import com.github.nagyesta.abortmission.core.healthcheck.impl.AbstractStageStatisticsCollector;
import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * The base component counting mission success/failure events in order to aid abort decision making.
 */
public abstract class ExternalStageStatisticsCollector extends AbstractStageStatisticsCollector implements StageStatistics {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalStageStatisticsCollector.class);
    private final String contextName;
    private final boolean countdown;

    /**
     * Default constructor only setting the matcher.
     *
     * @param contextName The name of the Abort-Mission context.
     * @param matcher     The matcher held by the evaluator owning this collector as well.
     * @param countdown   True if the collector will be used for countdown.
     */
    public ExternalStageStatisticsCollector(final String contextName,
                                            final MissionHealthCheckMatcher matcher,
                                            final boolean countdown) {
        super(matcher);
        this.contextName = contextName;
        this.countdown = countdown;
    }

    @Override
    public StageStatisticsSnapshot getSnapshot() {
        LOGGER.trace("Fetching snapshot for matcher {}", getMatcher());
        return doGetSnapshot(contextName, getMatcher(), countdown);
    }

    @Override
    public Stream<StageTimeMeasurement> timeSeriesStream() {
        LOGGER.trace("Fetching time series stream for matcher {}", getMatcher());
        return doFetchAll(contextName, getMatcher(), countdown).stream();
    }

    @Override
    public void logTimeMeasurement(final StageTimeMeasurement timeMeasurement) {
        doLogTimeMeasurement(contextName, getMatcher(), countdown, Objects.requireNonNull(timeMeasurement, "Measurement cannot be null."));
        if (countdown) {
            LOGGER.trace("Logging countdown {} event for class: {} with id: {}",
                    timeMeasurement.getResult(), timeMeasurement.getTestClassId(), timeMeasurement.getLaunchId());
        } else {
            LOGGER.trace("Logging mission {} event for class: {} and method: {} with id: {}",
                    timeMeasurement.getResult(), timeMeasurement.getTestClassId(),
                    timeMeasurement.getTestCaseId(), timeMeasurement.getLaunchId());
        }
    }

    /**
     * Obtains a snapshot from the implementing strongback for read-only use.
     *
     * @param contextName The name of the Abort-Mission context.
     * @param matcher     The matcher held by the evaluator owning this collector as well.
     * @param countdown   True if the collector will be used for countdown.
     * @return snapshot
     */
    @SuppressWarnings("checkstyle:HiddenField")
    protected abstract StageStatisticsSnapshot doGetSnapshot(String contextName,
                                                             MissionHealthCheckMatcher matcher,
                                                             boolean countdown);

    /**
     * Obtains the list of stage time measurements we have collected so far for a matcher.
     *
     * @param contextName The name of the Abort-Mission context.
     * @param matcher     The matcher held by the evaluator owning this collector as well.
     * @param countdown   True if the collector will be used for countdown.
     * @return list
     */
    @SuppressWarnings("checkstyle:HiddenField")
    protected abstract List<StageTimeMeasurement> doFetchAll(String contextName,
                                                             MissionHealthCheckMatcher matcher,
                                                             boolean countdown);

    /**
     * Captures the measurement instance provided for reporting purposes.
     *
     * @param contextName The name of the Abort-Mission context.
     * @param matcher     The matcher held by the evaluator owning this collector as well.
     * @param countdown   True if the collector will be used for countdown.
     * @param measurement The measurement we observed.
     */
    @SuppressWarnings("checkstyle:HiddenField")
    protected abstract void doLogTimeMeasurement(String contextName,
                                                 MissionHealthCheckMatcher matcher,
                                                 boolean countdown,
                                                 StageTimeMeasurement measurement);
}
