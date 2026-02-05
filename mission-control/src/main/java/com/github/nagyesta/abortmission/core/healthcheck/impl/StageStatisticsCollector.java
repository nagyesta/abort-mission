package com.github.nagyesta.abortmission.core.healthcheck.impl;

import com.github.nagyesta.abortmission.core.healthcheck.ReadOnlyStageStatistics;
import com.github.nagyesta.abortmission.core.healthcheck.StageStatistics;
import com.github.nagyesta.abortmission.core.healthcheck.StageStatisticsSnapshot;
import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * The component counting mission success/failure events to aid abort decision-making.
 */
public class StageStatisticsCollector extends AbstractStageStatisticsCollector implements StageStatistics {

    private static final Logger LOGGER = LoggerFactory.getLogger(StageStatisticsCollector.class);
    private final AtomicInteger failed;
    private final AtomicInteger succeeded;
    private final AtomicInteger aborted;
    private final AtomicInteger suppressed;
    private final List<StageTimeMeasurement> timeSeriesData = new CopyOnWriteArrayList<>();

    /**
     * Default constructor using 0 as baseline all across the measurements.
     * Allows clean starts.
     *
     * @param matcher The matcher held by the evaluator owning this collector as well.
     */
    public StageStatisticsCollector(final MissionHealthCheckMatcher matcher) {
        this(matcher, 0, 0, 0, 0);
    }

    /**
     * Constructor allowing us to start with some previous knowledge about successes or failures.
     *
     * @param matcher    The matcher held by the evaluator owning this collector as well.
     * @param failed     The number of times test or preparation was failed.
     * @param aborted    The number of times test or preparation was aborted.
     * @param succeeded  The number of times test or preparation was completed successfully.
     * @param suppressed The number of times abort/failure reporting was suppressed.
     */
    public StageStatisticsCollector(
            final MissionHealthCheckMatcher matcher,
            final int failed,
            final int aborted,
            final int succeeded,
            final int suppressed) {
        super(matcher);
        this.failed = new AtomicInteger(failed);
        this.aborted = new AtomicInteger(aborted);
        this.succeeded = new AtomicInteger(succeeded);
        this.suppressed = new AtomicInteger(suppressed);
    }

    @Override
    public StageStatisticsSnapshot getSnapshot() {
        return new DefaultStageStatisticsSnapshot(failed.get(), succeeded.get(), aborted.get(), suppressed.get());
    }

    @Override
    public Stream<StageTimeMeasurement> timeSeriesStream() {
        return timeSeriesData.stream();
    }

    @Override
    public void logTimeMeasurement(final StageTimeMeasurement timeMeasurement) {
        timeSeriesData.add(Objects.requireNonNull(timeMeasurement, "Measurement cannot be null."));
        if (StageTimeMeasurement.CLASS_ONLY.equals(timeMeasurement.getTestCaseId())) {
            LOGGER.trace("Logging countdown {} event for class: {} with id: {}",
                    timeMeasurement.getResult(), timeMeasurement.getTestClassId(), timeMeasurement.getLaunchId());
        } else {
            LOGGER.trace("Logging mission {} event for class: {} and method: {} with id: {}",
                    timeMeasurement.getResult(), timeMeasurement.getTestClassId(),
                    timeMeasurement.getTestCaseId(), timeMeasurement.getLaunchId());
        }
    }

    @Override
    public void logAndIncrement(final StageTimeMeasurement timeMeasurement) {
        logTimeMeasurement(timeMeasurement);
        switch (timeMeasurement.getResult()) {
            case FAILURE:
                failed.incrementAndGet();
                break;
            case ABORT:
                aborted.incrementAndGet();
                break;
            case SUCCESS:
                succeeded.incrementAndGet();
                break;
            case SUPPRESSED:
                suppressed.incrementAndGet();
                break;
            default:
                throw new UnsupportedOperationException("Unsupported stage result found: " + timeMeasurement.getResult());
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final ReadOnlyStageStatistics that)) {
            return false;
        }
        return super.equals(o) && this.getSnapshot().equals(that.getSnapshot());
    }

    @Override
    public int hashCode() {
        return Objects.hash(failed.get(), aborted.get(), succeeded.get(), suppressed.get());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StageStatisticsCollector.class.getSimpleName() + "[", "]")
                .add("failed=" + failed.get())
                .add("aborted=" + aborted.get())
                .add("succeeded=" + succeeded.get())
                .add("suppressed=" + suppressed.get())
                .add("timeSeries=" + timeSeriesData)
                .toString();
    }
}
