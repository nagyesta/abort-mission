package com.github.nagyesta.abortmission.core.healthcheck.impl;

import com.github.nagyesta.abortmission.core.healthcheck.ReadOnlyStageStatistics;
import com.github.nagyesta.abortmission.core.healthcheck.StatisticsLogger;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;

import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * The component counting mission success/failure events in order to aid abort decision making.
 */
public class StageStatisticsCollector implements ReadOnlyStageStatistics, StatisticsLogger {

    private final AtomicInteger failed;
    private final AtomicInteger succeeded;
    private final AtomicInteger aborted;
    private final AtomicInteger suppressed;
    private final Set<StageTimeMeasurement> timeSeriesData = new ConcurrentSkipListSet<>();

    /**
     * Default constructor using 0 as baseline all across the measurements.
     * Allows clean starts.
     */
    public StageStatisticsCollector() {
        this(0, 0, 0, 0);
    }

    /**
     * Constructor allowing us to start with some previous knowledge about successes or failures.
     *
     * @param failed     The number of times test or preparation was failed.
     * @param aborted    The number of times test or preparation was aborted.
     * @param succeeded  The number of times test or preparation was completed successfully.
     * @param suppressed The number of times abort/failure reporting was suppressed.
     */
    public StageStatisticsCollector(final int failed, final int aborted, final int succeeded, final int suppressed) {
        this.failed = new AtomicInteger(failed);
        this.aborted = new AtomicInteger(aborted);
        this.succeeded = new AtomicInteger(succeeded);
        this.suppressed = new AtomicInteger(suppressed);
    }

    @Override
    public int getTotal() {
        return failed.get() + aborted.get() + succeeded.get();
    }

    @Override
    public int getNotSuccessful() {
        return failed.get() + aborted.get();
    }

    @Override
    public int getFailed() {
        return failed.get();
    }

    @Override
    public int getSucceeded() {
        return succeeded.get();
    }

    @Override
    public int getSuppressed() {
        return suppressed.get();
    }

    @Override
    public int getAborted() {
        return aborted.get();
    }

    @Override
    public Stream<StageTimeMeasurement> timeSeriesStream() {
        return new TreeSet<>(timeSeriesData).stream();
    }

    @Override
    public void incrementFailed() {
        failed.incrementAndGet();
    }

    @Override
    public void incrementAborted() {
        aborted.incrementAndGet();
    }

    @Override
    public void incrementSucceeded() {
        succeeded.incrementAndGet();
    }

    @Override
    public void incrementSuppressed() {
        suppressed.incrementAndGet();
    }

    @Override
    public void logTimeMeasurement(final StageTimeMeasurement timeMeasurement) {
        timeSeriesData.add(Objects.requireNonNull(timeMeasurement, "Measurement cannot be null."));
    }

    @Override
    public void logAndIncrement(final StageTimeMeasurement timeMeasurement) {
        logTimeMeasurement(timeMeasurement);
        switch (timeMeasurement.getResult()) {
            case FAILURE:
                incrementFailed();
                break;
            case ABORT:
                incrementAborted();
                break;
            case SUCCESS:
                incrementSucceeded();
                break;
            case SUPPRESSED:
                incrementSuppressed();
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
        if (!(o instanceof ReadOnlyStageStatistics)) {
            return false;
        }
        final ReadOnlyStageStatistics that = (ReadOnlyStageStatistics) o;
        return failed.get() == that.getFailed()
                && aborted.get() == that.getAborted()
                && succeeded.get() == that.getSucceeded()
                && suppressed.get() == that.getSuppressed();
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
