package com.github.nagyesta.abortmission.strongback.rmi.stats;

import com.github.nagyesta.abortmission.core.healthcheck.StageStatisticsSnapshot;
import com.github.nagyesta.abortmission.core.healthcheck.impl.DefaultStageStatisticsSnapshot;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * RMI specific serializable DTO replacement of the StageStatisticsSnapshot.
 */
public class RmiStageStatisticsSnapshot implements Serializable {

    public static final long serialVersionUID = 1L;

    private int failed;
    private int succeeded;
    private int aborted;
    private int suppressed;

    /**
     * Default constructor for serialization.
     */
    public RmiStageStatisticsSnapshot() {
    }

    /**
     * Construct the instance and sets all the fields to let this instance represent the recorded outcome of a matcher.
     *
     * @param failed     The number of failed measurements.
     * @param succeeded  The number of success measurements.
     * @param aborted    The number of aborted measurements.
     * @param suppressed The number of suppressed measurements.
     */
    public RmiStageStatisticsSnapshot(final int failed, final int succeeded, final int aborted, final int suppressed) {
        this.failed = failed;
        this.succeeded = succeeded;
        this.aborted = aborted;
        this.suppressed = suppressed;
    }

    /**
     * See {@link StageStatisticsSnapshot#getFailed()}.
     *
     * @return count
     */
    public int getFailed() {
        return failed;
    }

    /**
     * Sets the number of failed cases.
     *
     * @param failed The count set.
     */
    public void setFailed(final int failed) {
        this.failed = failed;
    }

    /**
     * See {@link StageStatisticsSnapshot#getSucceeded()}.
     *
     * @return count
     */
    public int getSucceeded() {
        return succeeded;
    }

    /**
     * Sets the number of succeeded cases.
     *
     * @param succeeded The count set.
     */
    public void setSucceeded(final int succeeded) {
        this.succeeded = succeeded;
    }

    /**
     * See {@link StageStatisticsSnapshot#getSuppressed()}.
     *
     * @return count
     */
    public int getSuppressed() {
        return suppressed;
    }

    /**
     * Sets the number of suppressed cases.
     *
     * @param suppressed The count set.
     */
    public void setSuppressed(final int suppressed) {
        this.suppressed = suppressed;
    }

    /**
     * See {@link StageStatisticsSnapshot#getAborted()}.
     *
     * @return count
     */
    public int getAborted() {
        return aborted;
    }

    /**
     * Sets the number of aborted cases.
     *
     * @param aborted The count set.
     */
    public void setAborted(final int aborted) {
        this.aborted = aborted;
    }

    /**
     * Converts back to the generic variant.
     *
     * @return snapshot
     */
    public StageStatisticsSnapshot toStageStatisticsSnapshot() {
        return new DefaultStageStatisticsSnapshot(failed, succeeded, aborted, suppressed);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RmiStageStatisticsSnapshot)) {
            return false;
        }
        final RmiStageStatisticsSnapshot that = (RmiStageStatisticsSnapshot) o;
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
        return new StringJoiner(", ", RmiStageStatisticsSnapshot.class.getSimpleName() + "[", "]")
                .add("failed=" + failed)
                .add("succeeded=" + succeeded)
                .add("aborted=" + aborted)
                .add("suppressed=" + suppressed)
                .toString();
    }
}
