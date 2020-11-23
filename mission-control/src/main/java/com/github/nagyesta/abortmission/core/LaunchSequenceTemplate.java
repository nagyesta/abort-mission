package com.github.nagyesta.abortmission.core;

import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;
import com.github.nagyesta.abortmission.core.telemetry.watch.StageTimeStopwatch;

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

    private final Function<Method, Set<MissionHealthCheckEvaluator>> methodBasedEvaluatorLookup;

    /**
     * Constructor defining how the template can lookup the evaluators or abort the mission.
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
     * @return A stageTimeStopwatch started to measure execution times (won't be present if reporting already happened).
     */
    public Optional<StageTimeStopwatch> launchGoNoGo(final Class<?> testInstanceClass) {
        return this.performPreLaunchInit(testInstanceClass);
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
        countdownCompletedSuccessfully(classBasedEvaluatorLookup().apply(testClass), stopwatch);
    }

    /**
     * Marks completion of the test instance preparation.
     *
     * @param method The test method which is ready for execution.
     * @return A stageTimeStopwatch started to measure execution times (won't be present if reporting already happened).
     */
    public Optional<StageTimeStopwatch> launchImminent(final Method method) {
        return evaluateLaunchAbort(methodBasedEvaluatorLookup.apply(method), new StageTimeStopwatch(method),
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
        missionCompletedSuccessfully(methodBasedEvaluatorLookup.apply(method), stopwatch);
    }
}
