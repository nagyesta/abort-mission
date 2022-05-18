package com.github.nagyesta.abortmission.core.healthcheck.impl;

import com.github.nagyesta.abortmission.core.healthcheck.StageStatisticsSnapshot;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * Base implementation of the {@link StageStatisticsSnapshot} used by default.
 */
public class DefaultStageStatisticsSnapshot implements StageStatisticsSnapshot {

    private final int failed;
    private final int succeeded;
    private final int aborted;
    private final int suppressed;

    /**
     * Construct the instance and sets all the fields to let this instance represent the recorded outcome of a matcher.
     *
     * @param failed     The number of failed measurements.
     * @param succeeded  The number of success measurements.
     * @param aborted    The number of aborted measurements.
     * @param suppressed The number of suppressed measurements.
     */
    public DefaultStageStatisticsSnapshot(final int failed, final int succeeded, final int aborted, final int suppressed) {
        this.failed = failed;
        this.succeeded = succeeded;
        this.aborted = aborted;
        this.suppressed = suppressed;
    }

    @Override
    public int getTotal() {
        return failed + aborted + succeeded;
    }

    @Override
    public int getNotSuccessful() {
        return failed + aborted;
    }

    @Override
    public int getFailed() {
        return failed;
    }

    @Override
    public int getSucceeded() {
        return succeeded;
    }

    @Override
    public int getSuppressed() {
        return suppressed;
    }

    @Override
    public int getAborted() {
        return aborted;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultStageStatisticsSnapshot)) {
            return false;
        }
        final DefaultStageStatisticsSnapshot that = (DefaultStageStatisticsSnapshot) o;
        return failed == that.failed
                && succeeded == that.succeeded
                && aborted == that.aborted
                && suppressed == that.suppressed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(failed, succeeded, aborted, suppressed);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DefaultStageStatisticsSnapshot.class.getSimpleName() + "[", "]")
                .add("failed=" + failed)
                .add("succeeded=" + succeeded)
                .add("aborted=" + aborted)
                .add("suppressed=" + suppressed)
                .toString();
    }
}
