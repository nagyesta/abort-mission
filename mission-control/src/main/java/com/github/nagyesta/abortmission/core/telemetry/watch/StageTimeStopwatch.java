package com.github.nagyesta.abortmission.core.telemetry.watch;

import com.github.nagyesta.abortmission.core.telemetry.StageResult;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;

import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Stopwatch implementation used for milliseconds precision stage time measurements.
 */
public final class StageTimeStopwatch {

    private final String testId;
    private final long start;
    private final Map<StageResult, StageTimeMeasurement> cache = new EnumMap<>(StageResult.class);

    /**
     * Creates a new instance using a custom string identifier.
     *
     * @param testId The id of the test class/method.
     */
    public StageTimeStopwatch(final String testId) {
        this.testId = testId;
        this.start = System.currentTimeMillis();
    }

    /**
     * Creates a new instance using a custom string identifier.
     *
     * @param testClass The class of test.
     */
    public StageTimeStopwatch(final Class<?> testClass) {
        this(Objects.requireNonNull(testClass, "Test class cannot be null.").getName());
    }

    /**
     * Creates a new instance using a custom string identifier.
     *
     * @param testMethod The method of test.
     */
    public StageTimeStopwatch(final Method testMethod) {
        this(Objects.requireNonNull(testMethod, "Test method cannot be null.")
                .getDeclaringClass().getName() + "#" + testMethod.getName());
    }

    public Function<StageResult, StageTimeMeasurement> stop() {
        final long end = System.currentTimeMillis();
        return result -> cache.computeIfAbsent(result, r -> new StageTimeMeasurement(testId, r, start, end));
    }
}
