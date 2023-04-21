package com.github.nagyesta.abortmission.core.telemetry;

import com.github.nagyesta.abortmission.core.telemetry.watch.StageTimeMeasurementDataProvider;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Builder class for {@link StageTimeMeasurement}.
 */
@SuppressWarnings("checkstyle:HiddenField")
public final class StageTimeMeasurementBuilder implements StageTimeMeasurementDataProvider {
    private UUID launchId;
    private StageResult result;
    private long start;
    private long end;
    private String testClassId;
    private String testCaseId;
    private String threadName;
    private String displayName;
    private String throwableClass;
    private String throwableMessage;
    private List<String> stackTrace;

    private StageTimeMeasurementBuilder() {
        this.threadName = Thread.currentThread().getName();
    }

    /**
     * Creates a new builder instance.
     *
     * @return a new builder instance.
     */
    public static StageTimeMeasurementBuilder builder() {
        return new StageTimeMeasurementBuilder();
    }

    @Override
    public UUID getLaunchId() {
        return launchId;
    }

    /**
     * Sets the launch id.
     *
     * @param launchId The launch id.
     * @return this
     */
    public StageTimeMeasurementBuilder setLaunchId(final UUID launchId) {
        this.launchId = launchId;
        return this;
    }

    /**
     * Returns the result.
     *
     * @return The result.
     */
    public StageResult getResult() {
        return result;
    }

    /**
     * Sets the result.
     *
     * @param result The result.
     * @return this
     */
    public StageTimeMeasurementBuilder setResult(final StageResult result) {
        this.result = result;
        return this;
    }

    @Override
    public long getStart() {
        return start;
    }

    /**
     * Sets the start time.
     *
     * @param start The start time.
     * @return this
     */
    public StageTimeMeasurementBuilder setStart(final long start) {
        this.start = start;
        return this;
    }

    @Override
    public Long getEnd() {
        return end;
    }

    /**
     * Sets the end time.
     *
     * @param end The end time.
     * @return this
     */
    public StageTimeMeasurementBuilder setEnd(final long end) {
        this.end = end;
        return this;
    }

    @Override
    public String getTestClassId() {
        return testClassId;
    }

    /**
     * Sets the test class id.
     *
     * @param testClassId The test class id.
     * @return this
     */
    public StageTimeMeasurementBuilder setTestClassId(final String testClassId) {
        this.testClassId = testClassId;
        return this;
    }

    @Override
    public String getTestCaseId() {
        return testCaseId;
    }

    /**
     * Sets the test case id.
     *
     * @param testCaseId The test case id.
     * @return this
     */
    public StageTimeMeasurementBuilder setTestCaseId(final String testCaseId) {
        this.testCaseId = testCaseId;
        return this;
    }

    @Override
    public String getThreadName() {
        return threadName;
    }

    /**
     * Sets the thread name.
     *
     * @param threadName The thread name.
     * @return this
     */
    public StageTimeMeasurementBuilder setThreadName(final String threadName) {
        this.threadName = threadName;
        return this;
    }

    @Override
    public String getDisplayName() {
        return Objects.requireNonNullElse(displayName, testCaseId);
    }

    /**
     * Sets the display name.
     *
     * @param displayName The display name.
     * @return this
     */
    public StageTimeMeasurementBuilder setDisplayName(final String displayName) {
        this.displayName = displayName;
        return this;
    }

    @Override
    public String getThrowableClass() {
        return throwableClass;
    }

    /**
     * Sets the throwable class.
     *
     * @param throwableClass The throwable class.
     * @return this
     */
    public StageTimeMeasurementBuilder setThrowableClass(final String throwableClass) {
        this.throwableClass = throwableClass;
        return this;
    }

    @Override
    public String getThrowableMessage() {
        return throwableMessage;
    }

    /**
     * Sets the throwable message.
     *
     * @param throwableMessage The throwable message.
     * @return this
     */
    public StageTimeMeasurementBuilder setThrowableMessage(final String throwableMessage) {
        this.throwableMessage = throwableMessage;
        return this;
    }

    @Override
    public List<String> getStackTrace() {
        return stackTrace;
    }

    /**
     * Sets the stack trace.
     *
     * @param stackTrace The stack trace.
     * @return this
     */
    public StageTimeMeasurementBuilder setStackTrace(final List<String> stackTrace) {
        this.stackTrace = stackTrace;
        return this;
    }

    public StageTimeMeasurement build() {
        return new StageTimeMeasurement(this, getResult());
    }
}
