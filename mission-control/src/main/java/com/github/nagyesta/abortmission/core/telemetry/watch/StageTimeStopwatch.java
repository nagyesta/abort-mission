package com.github.nagyesta.abortmission.core.telemetry.watch;

import com.github.nagyesta.abortmission.core.telemetry.StageResult;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

import static com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement.CLASS_ONLY;

/**
 * Stopwatch implementation used for milliseconds precision stage time measurements.
 */
public final class StageTimeStopwatch {

    private final UUID launchId;
    private final String testClassId;
    private final String testCaseId;
    private final long start;
    private final Map<StageResult, StageTimeMeasurement> cache = new EnumMap<>(StageResult.class);

    /**
     * Creates a new instance using a custom string identifier.
     *
     * @param testClassId The id of the test class.
     * @param testCaseId  The id of the test method in the scope of the class.
     */
    public StageTimeStopwatch(final String testClassId, final String testCaseId) {
        this.testClassId = Objects.requireNonNull(testClassId, "Test class cannot be null.");
        this.testCaseId = Optional.ofNullable(testCaseId).orElse(CLASS_ONLY);
        this.start = System.currentTimeMillis();
        this.launchId = UUID.randomUUID();
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

    public Function<StageResult, StageTimeMeasurement> stop() {
        final long end = System.currentTimeMillis();
        return result -> cache
                .computeIfAbsent(result, r -> new StageTimeMeasurement(launchId, testClassId, testCaseId, r, start, end));
    }
}
