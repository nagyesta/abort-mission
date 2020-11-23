package com.github.nagyesta.abortmission.core.telemetry.stats;

import com.github.nagyesta.abortmission.core.telemetry.StageResult;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;

import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;

/**
 * Captures the core timings and outcome of one stage of the launch (countdown/mission).
 */
public final class TestRunTelemetry implements Comparable<TestRunTelemetry> {

    private final UUID launchId;
    private final StageResult result;
    private final long start;
    private final long end;

    /**
     * The constructor allowing us to create a new instance capturing the time measured data.
     *
     * @param original The original {@link StageTimeMeasurement} we are converting.
     */
    public TestRunTelemetry(final StageTimeMeasurement original) {
        Objects.requireNonNull(original, "Original cannot be null.");
        this.launchId = original.getLaunchId();
        this.result = original.getResult();
        this.start = original.getStart();
        this.end = original.getEnd();
    }

    public UUID getLaunchId() {
        return launchId;
    }

    public StageResult getResult() {
        return result;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public long getDurationMillis() {
        return end - start;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(final TestRunTelemetry o) {
        return Comparator
                .nullsLast(
                        Comparator.comparingLong(TestRunTelemetry::getStart)
                                .thenComparingLong(TestRunTelemetry::getEnd)
                                .thenComparing(TestRunTelemetry::getLaunchId)
                                .thenComparing(TestRunTelemetry::getResult)
                )
                .compare(this, o);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TestRunTelemetry)) {
            return false;
        }
        final TestRunTelemetry that = (TestRunTelemetry) o;
        return launchId.equals(that.launchId)
                && start == that.start
                && end == that.end
                && result == that.result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(launchId, result, start, end);
    }

}
