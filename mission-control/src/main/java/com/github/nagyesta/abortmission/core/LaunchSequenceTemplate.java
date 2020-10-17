package com.github.nagyesta.abortmission.core;

import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;

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
public final class LaunchSequenceTemplate extends AbstractLaunchSequenceTemplate {

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
     */
    public void launchGoNoGo(final Class<?> testInstanceClass) {
        this.performPreLaunchInit(testInstanceClass);
    }

    /**
     * Marks completion of the test instance preparation.
     *
     * @param method The test method which is ready for execution.
     */
    public void launchImminent(final Method method) {
        preLaunchInitComplete(() -> annotationContextEvaluator().isAbortSuppressed(method),
                methodBasedEvaluatorLookup.apply(method));
    }

    /**
     * Indicates that a failure happened during test run.
     *
     * @param method    The method we executed.
     * @param rootCause The exception identified as root cause of the issue.
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public void launchFailure(final Method method, final Optional<Throwable> rootCause) {
        failureDetected(rootCause,
                methodBasedEvaluatorLookup.apply(method),
                annotationContextEvaluator().findSuppressedExceptions(method));
    }

    /**
     * Wraps up the mission by logging a successful run.
     *
     * @param method The test method which was executed.
     */
    public void launchSuccess(final Method method) {
        completedSuccessfully(methodBasedEvaluatorLookup.apply(method));
    }

}
