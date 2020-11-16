package com.github.nagyesta.abortmission.core.telemetry;

import java.util.Comparator;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Captures the timings and outcome of one stage of the launch (countdown/mission).
 */
public final class StageTimeMeasurement implements Comparable<StageTimeMeasurement> {

    private final StageResult result;
    private final long start;
    private final long end;
    private final String testId;

    /**
     * The constructor allowing us to create a new instance capturing the time measured data.
     *
     * @param testId The unique Id of the test class or test method (it is fine to run the same
     *               preparation multiple times and capture different measurements, it will only
     *               be used for the report generation.
     * @param result The outcome of the stage execution.
     * @param start  The start time of the stage execution.
     * @param end    The end time of the stage execution.
     */
    public StageTimeMeasurement(final String testId, final StageResult result, final long start, final long end) {
        this.testId = Objects.requireNonNull(testId, "TestId cannot be null.");
        this.result = Objects.requireNonNull(result, "Result cannot be null.");
        this.start = start;
        this.end = end;
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

    public String getTestId() {
        return testId;
    }

    public long getDurationMillis() {
        return end - start;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(final StageTimeMeasurement o) {
        return Comparator
                .nullsLast(
                        Comparator.comparingLong(StageTimeMeasurement::getStart)
                                .thenComparingLong(StageTimeMeasurement::getEnd)
                                .thenComparing(StageTimeMeasurement::getResult)
                                .thenComparing(StageTimeMeasurement::getTestId))
                .compare(this, o);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StageTimeMeasurement)) {
            return false;
        }
        final StageTimeMeasurement that = (StageTimeMeasurement) o;
        return start == that.start
                && end == that.end
                && result == that.result
                && testId.equals(that.testId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, start, end, testId);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StageTimeMeasurement.class.getSimpleName() + "[", "]")
                .add("result=" + result)
                .add("start=" + start)
                .add("end=" + end)
                .add("testId='" + testId + "'")
                .toString();
    }

}
