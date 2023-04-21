package com.github.nagyesta.abortmission.core;

import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;
import com.github.nagyesta.abortmission.core.telemetry.watch.StageTimeStopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static com.github.nagyesta.abortmission.core.MissionControl.annotationContextEvaluator;

/**
 * Provides support for the whole lifecycle of the mission in a reasonably flexible format to allow easy test
 * framework integration.
 */
public class LaunchSequenceTemplate extends AbstractLaunchSequenceTemplate {

    private static final Logger LOGGER = LoggerFactory.getLogger(LaunchSequenceTemplate.class);
    private final Function<Method, Set<MissionHealthCheckEvaluator>> methodBasedEvaluatorLookup;

    /**
     * Constructor defining how the template can look up the evaluators or abort the mission.
     *
     * @param abortSequence              The {@link Runnable} which aborts the tests (usually by throwing an exception)
     * @param classBasedEvaluatorLookup  The {@link Function} that will be used for evaluator lookup when the tested component is a class.
     * @param methodBasedEvaluatorLookup The {@link Function} that will be used for evaluator lookup when the tested component is a method.
     */
    public LaunchSequenceTemplate(final Runnable abortSequence,
                                  final Function<Class<?>, Set<MissionHealthCheckEvaluator>> classBasedEvaluatorLookup,
                                  final Function<Method, Set<MissionHealthCheckEvaluator>> methodBasedEvaluatorLookup) {
        super(abortSequence, classBasedEvaluatorLookup);
        this.methodBasedEvaluatorLookup = Objects.requireNonNull(methodBasedEvaluatorLookup, "Method evaluator cannot be null.");
    }

    /**
     * The initial step of the mission before test instance initialization would start.
     *
     * @param testInstanceClass The class which will act as the test instance.
     * @param displayName       The display name of the test case.
     * @return A stageTimeStopwatch started to measure execution times (won't be present if reporting already happened).
     */
    public Optional<StageTimeStopwatch> launchGoNoGo(final Class<?> testInstanceClass, final String displayName) {
        LOGGER.debug("Preparing countdown for class: {}", testInstanceClass.getSimpleName());
        return this.performPreLaunchInit(testInstanceClass, displayName);
    }

    /**
     * Indicates that a failure happened during test post-processing.
     *
     * @param testClass The test class which was post-processed.
     * @param rootCause The exception identified as root cause of the issue.
     * @param stopwatch Captures when did the countdown start.
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public void countdownFailure(final Class<?> testClass, final Optional<Throwable> rootCause,
                                 final Optional<StageTimeStopwatch> stopwatch) {
        LOGGER.debug("Countdown failed for class: {} due to root cause: {}", testClass.getSimpleName(), rootCause);
        countdownFailureDetected(classBasedEvaluatorLookup().apply(testClass), stopwatch, rootCause,
                annotationContextEvaluator().findSuppressedExceptions(testClass)
        );
    }

    /**
     * Wraps up the post-processing by logging a successful countdown.
     *
     * @param testClass The test class which was post-processed.
     * @param stopwatch Captures when did the countdown start.
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public void countdownSuccess(final Class<?> testClass, final Optional<StageTimeStopwatch> stopwatch) {
        LOGGER.debug("Countdown completed for class: {}", testClass.getSimpleName());
        countdownCompletedSuccessfully(classBasedEvaluatorLookup().apply(testClass), stopwatch);
    }

    /**
     * Marks completion of the test instance preparation.
     *
     * @param method      The test method which is ready for execution.
     * @param displayName The display name of the test case.
     * @return A stageTimeStopwatch started to measure execution times (won't be present if reporting already happened).
     */
    public Optional<StageTimeStopwatch> launchImminent(final Method method, final String displayName) {
        LOGGER.debug("Preparing mission for class: {} method: {}", method.getDeclaringClass().getSimpleName(), method.getName());
        return evaluateLaunchAbort(methodBasedEvaluatorLookup.apply(method),
                new StageTimeStopwatch(method).overrideDisplayName(displayName),
                () -> annotationContextEvaluator().isAbortSuppressed(method)
        );
    }

    /**
     * Indicates that a failure happened during test run.
     *
     * @param method    The method we executed.
     * @param rootCause The exception identified as root cause of the issue.
     * @param stopwatch Captures when did the launch start.
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public void launchFailure(final Method method, final Optional<Throwable> rootCause, final Optional<StageTimeStopwatch> stopwatch) {
        LOGGER.debug("Mission failed for class: {} method: {} due to: {}",
                method.getDeclaringClass().getSimpleName(), method.getName(), rootCause);
        missionFailureDetected(methodBasedEvaluatorLookup.apply(method), stopwatch,
                rootCause, annotationContextEvaluator().findSuppressedExceptions(method)
        );
    }

    /**
     * Wraps up the mission by logging a successful run.
     *
     * @param method    The test method which was executed.
     * @param stopwatch Captures when did the launch start.
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public void launchSuccess(final Method method, final Optional<StageTimeStopwatch> stopwatch) {
        LOGGER.debug("Mission successful for class: {} method: {}", method.getDeclaringClass().getSimpleName(), method.getName());
        missionCompletedSuccessfully(methodBasedEvaluatorLookup.apply(method), stopwatch);
    }
}
