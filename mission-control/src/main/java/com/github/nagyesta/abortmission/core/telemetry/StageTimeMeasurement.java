package com.github.nagyesta.abortmission.core.telemetry;

import com.github.nagyesta.abortmission.core.telemetry.watch.StageTimeMeasurementDataProvider;

import java.util.*;

/**
 * Captures the timings and outcome of one stage of the launch (countdown/mission).
 */
public final class StageTimeMeasurement implements Comparable<StageTimeMeasurement> {
    /**
     * Name used to indicate that this is the class level measurement and not a test method.
     */
    public static final String CLASS_ONLY = " - CLASS - ";
    /**
     * The line separator used when stack trace is serialized as a single string.
     */
    public static final String STACKTRACE_LINE_SEPARATOR = "|";
    private final StageResult result;
    private final long start;
    private final long end;
    private final String testClassId;
    private final String testCaseId;
    private final UUID launchId;
    private final String displayName;
    private final String threadName;
    private final String throwableClass;
    private final String throwableMessage;
    private final List<String> stackTrace;

    /**
     * The constructor allowing us to create a new instance capturing the time measured data.
     *
     * @param measurementDataProvider The data provider for the measurement.
     * @param stageResult             The outcome of the stage execution.
     */
    public StageTimeMeasurement(
            final StageTimeMeasurementDataProvider measurementDataProvider,
            final StageResult stageResult) {
        Objects.requireNonNull(measurementDataProvider, "StageTimeMeasurementDataProvider cannot be null.");
        this.launchId = Objects.requireNonNull(measurementDataProvider.getLaunchId(), "Launch Id cannot be null.");
        this.testClassId = Objects.requireNonNull(measurementDataProvider.getTestClassId(), "TestClassId cannot be null.");
        this.testCaseId = Objects.requireNonNull(measurementDataProvider.getTestCaseId(), "TestCaseId cannot be null.");
        this.displayName = Objects.requireNonNull(measurementDataProvider.getDisplayName(), "DisplayName cannot be null.");
        this.result = Objects.requireNonNull(stageResult, "StageResult cannot be null.");
        this.start = measurementDataProvider.getStart();
        this.end = Objects.requireNonNull(measurementDataProvider.getEnd(), "End cannot be null.");
        this.threadName = Objects.requireNonNull(measurementDataProvider.getThreadName(), "ThreadName cannot be null.");
        this.throwableClass = measurementDataProvider.getThrowableClass();
        this.throwableMessage = measurementDataProvider.getThrowableMessage();
        this.stackTrace = Optional.ofNullable(measurementDataProvider.getStackTrace()).map(List::copyOf).orElse(null);
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

    public String getDisplayName() {
        return displayName;
    }

    public String getThreadName() {
        return threadName;
    }

    public String getThrowableClass() {
        return throwableClass;
    }

    public String getThrowableMessage() {
        return throwableMessage;
    }

    public List<String> getStackTrace() {
        return stackTrace;
    }

    public String getStackTraceAsString() {
        return Optional.ofNullable(stackTrace)
                .map(list -> String.join(STACKTRACE_LINE_SEPARATOR, list))
                .orElse(null);
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

    @Override
    public int hashCode() {
        return Objects.hash(result, start, end, testClassId, testCaseId, launchId, displayName, threadName, throwableClass,
                throwableMessage, stackTrace);
    }

    private boolean areFieldValuesEqual(final StageTimeMeasurement that) {
        return start == that.start
                && end == that.end
                && result == that.result
                && Objects.equals(testClassId, that.testClassId)
                && Objects.equals(testCaseId, that.testCaseId)
                && Objects.equals(launchId, that.launchId)
                && Objects.equals(displayName, that.displayName)
                && Objects.equals(threadName, that.threadName)
                && Objects.equals(throwableClass, that.throwableClass)
                && Objects.equals(throwableMessage, that.throwableMessage)
                && Objects.equals(stackTrace, that.stackTrace);
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
                .add("threadName=" + threadName)
                .add("throwableClass=" + throwableClass)
                .toString();
    }

}
