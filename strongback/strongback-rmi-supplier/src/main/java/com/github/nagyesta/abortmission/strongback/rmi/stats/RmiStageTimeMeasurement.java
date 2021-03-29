package com.github.nagyesta.abortmission.strongback.rmi.stats;

import com.github.nagyesta.abortmission.core.telemetry.StageResult;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

/**
 * Captures the timings and outcome of one stage of the launch (countdown/mission).
 */
public final class RmiStageTimeMeasurement implements Comparable<RmiStageTimeMeasurement>, Serializable {

    public static final long serialVersionUID = 1L;
    /**
     * Name used to indicate that this is the class level measurement and not a test method.
     */
    private StageResult result;
    private long start;
    private long end;
    private String testClassId;
    private String testCaseId;
    private UUID launchId;

    /**
     * Default contructor for serialization.
     */
    public RmiStageTimeMeasurement() {
    }

    /**
     * The constructor allowing us to create a new instance capturing the time measured data.
     *
     * @param measurement The measurement we captured (and need to convert).
     */
    public RmiStageTimeMeasurement(final StageTimeMeasurement measurement) {
        this.launchId = Objects.requireNonNull(measurement, "Measurement cannot be null.").getLaunchId();
        this.testClassId = measurement.getTestClassId();
        this.testCaseId = measurement.getTestCaseId();
        this.result = measurement.getResult();
        this.start = measurement.getStart();
        this.end = measurement.getEnd();
    }

    public UUID getLaunchId() {
        return launchId;
    }

    public void setLaunchId(final UUID launchId) {
        this.launchId = launchId;
    }

    public StageResult getResult() {
        return result;
    }

    public void setResult(final StageResult result) {
        this.result = result;
    }

    public long getStart() {
        return start;
    }

    public void setStart(final long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(final long end) {
        this.end = end;
    }

    public String getTestClassId() {
        return testClassId;
    }

    public void setTestClassId(final String testClassId) {
        this.testClassId = testClassId;
    }

    public String getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(final String testCaseId) {
        this.testCaseId = testCaseId;
    }

    /**
     * Converts the values back to {@link StageTimeMeasurement}.
     *
     * @return measurement
     */
    public StageTimeMeasurement toStageTimeMeasurement() {
        return new StageTimeMeasurement(launchId, testClassId, testCaseId, result, start, end);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(final RmiStageTimeMeasurement o) {
        return Comparator
                .nullsLast(
                        Comparator.comparingLong(RmiStageTimeMeasurement::getStart)
                                .thenComparingLong(RmiStageTimeMeasurement::getEnd)
                                .thenComparing(RmiStageTimeMeasurement::getTestClassId)
                                .thenComparing(RmiStageTimeMeasurement::getTestCaseId)
                                .thenComparing(RmiStageTimeMeasurement::getLaunchId)
                                .thenComparing(RmiStageTimeMeasurement::getResult)
                )
                .compare(this, o);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RmiStageTimeMeasurement)) {
            return false;
        }
        return areFieldValuesEqual((RmiStageTimeMeasurement) o);
    }

    private boolean areFieldValuesEqual(final RmiStageTimeMeasurement that) {
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
        return new StringJoiner(", ", RmiStageTimeMeasurement.class.getSimpleName() + "[", "]")
                .add("launchId='" + launchId + "'")
                .add("testClassId='" + testClassId + "'")
                .add("testCaseId='" + testCaseId + "'")
                .add("result=" + result)
                .add("start=" + start)
                .add("end=" + end)
                .toString();
    }

}
