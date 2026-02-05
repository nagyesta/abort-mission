package com.github.nagyesta.abortmission.core.telemetry.watch;

import com.github.nagyesta.abortmission.core.AbortMissionGlobalConfiguration;
import com.github.nagyesta.abortmission.core.telemetry.StageResult;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

import static com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement.CLASS_ONLY;

/**
 * Stopwatch implementation used for milliseconds precision stage time measurements.
 */
public final class StageTimeStopwatch implements StageTimeMeasurementDataProvider {

    private static final String FILTERED_ITEM = "< filtered >";
    private final UUID launchId;
    private final String testClassId;
    private final String testCaseId;
    private final long start;
    private Long end;
    private String displayName;
    private String threadName;
    private String throwableClass;
    private String throwableMessage;
    private List<String> stackTrace;
    private final Map<StageResult, StageTimeMeasurement> cache = new EnumMap<>(StageResult.class);

    /**
     * Creates a new instance using a custom string identifier.
     *
     * @param testClassId The id of the test class.
     * @param testCaseId  The id of the test method in the scope of the class.
     */
    public StageTimeStopwatch(
            final String testClassId,
            final String testCaseId) {
        this.testClassId = Objects.requireNonNull(testClassId, "Test class cannot be null.");
        this.testCaseId = Optional.ofNullable(testCaseId).orElse(CLASS_ONLY);
        this.start = System.currentTimeMillis();
        this.launchId = UUID.randomUUID();
        this.threadName = Thread.currentThread().getName();
        this.displayName = this.testCaseId;
    }

    /**
     * Creates a new instance using a custom string identifier.
     *
     * @param testClass The class of test.
     */
    public StageTimeStopwatch(final Class<?> testClass) {
        this(Objects.requireNonNull(testClass, "Test class cannot be null.").getName(), null);
    }

    /**
     * Creates a new instance using a custom string identifier.
     *
     * @param testMethod The method of test.
     */
    public StageTimeStopwatch(final Method testMethod) {
        this(Objects.requireNonNull(testMethod, "Test method cannot be null.")
                .getDeclaringClass().getName(), testMethod.getName());
    }

    /**
     * Overrides the thread name used for the measurement.
     * This can add additional information in the case of parallel test execution.
     *
     * @param threadName The name of the thread.
     * @return this
     */
    @SuppressWarnings("checkstyle:HiddenField")
    public StageTimeStopwatch overrideThreadName(final String threadName) {
        this.threadName = threadName;
        return this;
    }

    /**
     * Overrides the display name used for the measurement.
     * This information will help the user to identify the test case in the reports,
     * including the parameters used in the case of parameterized tests.
     *
     * @param displayName The name of the thread.
     * @return this
     */
    @SuppressWarnings("checkstyle:HiddenField")
    public StageTimeStopwatch overrideDisplayName(final String displayName) {
        this.displayName = displayName;
        return this;
    }

    /**
     * Overrides the throwable information used for the measurement (if it is present).
     * This is intended to be called only once per instance, but it is not enforced.
     *
     * @param throwable The throwable instance.
     * @return this
     */
    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "checkstyle:MagicNumber"})
    public StageTimeStopwatch addThrowable(final Optional<Throwable> throwable) {
        throwable.ifPresent(t -> {
            this.throwableClass = Objects.requireNonNull(t, "Throwable must not be null.").getClass().getName();
            this.throwableMessage = t.getMessage();
            this.stackTrace = Arrays.stream(t.getStackTrace())
                    .map(e -> {
                        if (AbortMissionGlobalConfiguration.shared().getStackTraceFilter().test(e)) {
                            return e.toString();
                        } else {
                            return FILTERED_ITEM;
                        }
                    })
                    .reduce(new ArrayList<>(), (list, s) -> {
                        if (list.size() == AbortMissionGlobalConfiguration.shared().getStackTraceDepth()) {
                            return list;
                        }
                        if (list.isEmpty() || !list.get(list.size() - 1).equals(FILTERED_ITEM) || !s.equals(FILTERED_ITEM)) {
                            list.add(s);
                        }
                        return list;
                    }, (l1, l2) -> {
                        throw new UnsupportedOperationException("Parallel stream not supported.");
                    });
        });
        return this;
    }

    @Override
    public UUID getLaunchId() {
        return launchId;
    }

    @Override
    public String getTestClassId() {
        return testClassId;
    }

    @Override
    public String getTestCaseId() {
        return testCaseId;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public long getStart() {
        return start;
    }

    @Override
    public Long getEnd() {
        return end;
    }

    @Override
    public String getThreadName() {
        return threadName;
    }

    @Override
    public String getThrowableClass() {
        return throwableClass;
    }

    @Override
    public String getThrowableMessage() {
        return throwableMessage;
    }

    @Override
    public List<String> getStackTrace() {
        return stackTrace;
    }

    public Function<StageResult, StageTimeMeasurement> stop() {
        end = System.currentTimeMillis();
        return result -> cache.computeIfAbsent(result, r -> new StageTimeMeasurement(this, r));
    }
}
