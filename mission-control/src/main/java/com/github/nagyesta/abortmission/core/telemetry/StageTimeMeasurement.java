package com.github.nagyesta.abortmission.core.telemetry;

import java.beans.ConstructorProperties;
import java.util.Comparator;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

/**
 * Captures the timings and outcome of one stage of the launch (countdown/mission).
 */
public final class StageTimeMeasurement implements Comparable<StageTimeMeasurement> {
    /**
     * Name used to indicate that this is the class level measurement and not a test method.
     */
    public static final String CLASS_ONLY = " - CLASS - ";
    private final StageResult result;
    private final long start;
    private final long end;
    private final String testClassId;
    private final String testCaseId;
    private final UUID launchId;

    /**
     * The constructor allowing us to create a new instance capturing the time measured data.
     *
     * @param launchId    The unique ID of the test method execution (must be unique even in case the same test is re-run).
     * @param testClassId The unique ID of the test class.
     * @param testCaseId  The unique ID of the test case (within the test class).
     * @param result      The outcome of the stage execution.
     * @param start       The start time of the stage execution.
     * @param end         The end time of the stage execution.
     */
    @ConstructorProperties({"launchId", "testClassId", "testCaseId", "result", "start", "end"})
    public StageTimeMeasurement(final UUID launchId,
                                final String testClassId,
                                final String testCaseId,
                                final StageResult result,
                                final long start,
                                final long end) {
        this.launchId = Objects.requireNonNull(launchId, "Launch Id cannot be null.");
        this.testClassId = Objects.requireNonNull(testClassId, "TestClassId cannot be null.");
        this.testCaseId = Objects.requireNonNull(testCaseId, "TestCaseId cannot be null.");
        this.result = Objects.requireNonNull(result, "Result cannot be null.");
        this.start = start;
        this.end = end;
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

    public String getTestClassId() {
        return testClassId;
    }

    public String getTestCaseId() {
        return testCaseId;
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
                                .thenComparing(StageTimeMeasurement::getTestClassId)
                                .thenComparing(StageTimeMeasurement::getTestCaseId)
                                .thenComparing(StageTimeMeasurement::getLaunchId)
                                .thenComparing(StageTimeMeasurement::getResult)
                )
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
        return areFieldValuesEqual((StageTimeMeasurement) o);
    }

    private boolean areFieldValuesEqual(final StageTimeMeasurement that) {
        return launchId.equals(that.launchId)
                && start == that.start
                && end == that.end
                && result == that.result
                && testClassId.equals(that.testClassId)
                && testCaseId.equals(that.testCaseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(launchId, result, start, end, testClassId, testCaseId);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StageTimeMeasurement.class.getSimpleName() + "[", "]")
                .add("launchId='" + launchId + "'")
                .add("testClassId='" + testClassId + "'")
                .add("testCaseId='" + testCaseId + "'")
                .add("result=" + result)
                .add("start=" + start)
                .add("end=" + end)
                .toString();
    }

}
