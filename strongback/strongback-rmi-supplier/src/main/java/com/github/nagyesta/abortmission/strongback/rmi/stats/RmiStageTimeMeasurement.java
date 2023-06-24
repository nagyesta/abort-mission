package com.github.nagyesta.abortmission.strongback.rmi.stats;

import com.github.nagyesta.abortmission.core.telemetry.StageResult;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurementBuilder;

import java.io.Serializable;
import java.util.*;

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
    private String displayName;
    private String threadName;
    private String throwableClass;
    private String throwableMessage;
    private List<String> stackTrace;

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
        this.displayName = measurement.getDisplayName();
        this.threadName = measurement.getThreadName();
        this.throwableClass = measurement.getThrowableClass();
        this.throwableMessage = measurement.getThrowableMessage();
        this.stackTrace = measurement.getStackTrace();
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

    public String getDisplayName() {
        return displayName;
    }

    public RmiStageTimeMeasurement setDisplayName(final String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getThreadName() {
        return threadName;
    }

    public RmiStageTimeMeasurement setThreadName(final String threadName) {
        this.threadName = threadName;
        return this;
    }

    public String getThrowableClass() {
        return throwableClass;
    }

    public RmiStageTimeMeasurement setThrowableClass(final String throwableClass) {
        this.throwableClass = throwableClass;
        return this;
    }

    public String getThrowableMessage() {
        return throwableMessage;
    }

    public RmiStageTimeMeasurement setThrowableMessage(final String throwableMessage) {
        this.throwableMessage = throwableMessage;
        return this;
    }

    public List<String> getStackTrace() {
        return stackTrace;
    }

    public RmiStageTimeMeasurement setStackTrace(final List<String> stackTrace) {
        this.stackTrace = stackTrace;
        return this;
    }

    /**
     * Converts the values back to {@link StageTimeMeasurement}.
     *
     * @return measurement
     */
    public StageTimeMeasurement toStageTimeMeasurement() {
        return StageTimeMeasurementBuilder.builder()
                .setLaunchId(launchId)
                .setTestClassId(testClassId)
                .setTestCaseId(testCaseId)
                .setResult(result)
                .setStart(start)
                .setEnd(end)
                .setDisplayName(displayName)
                .setThreadName(threadName)
                .setThrowableClass(throwableClass)
                .setThrowableMessage(throwableMessage)
                .setStackTrace(stackTrace)
                .build();
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

    @Override
    public int hashCode() {
        return Objects.hash(result, start, end, testClassId, testCaseId, launchId, displayName, threadName, throwableClass,
                throwableMessage, stackTrace);
    }

    private boolean areFieldValuesEqual(final RmiStageTimeMeasurement that) {
        return start == that.start
                && end == that.end
                && result == that.result
                && Objects.equals(testClassId, that.getTestClassId())
                && Objects.equals(testCaseId, that.getTestCaseId())
                && Objects.equals(launchId, that.getLaunchId())
                && Objects.equals(displayName, that.getDisplayName())
                && Objects.equals(threadName, that.getThreadName())
                && Objects.equals(throwableClass, that.getThrowableClass())
                && Objects.equals(throwableMessage, that.getThrowableMessage())
                && Objects.equals(stackTrace, that.getStackTrace());
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
                .add("threadName='" + threadName + "'")
                .add("displayName='" + displayName + "'")
                .add("throwableClass='" + throwableClass + "'")
                .toString();
    }

}
