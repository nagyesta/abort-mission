package com.github.nagyesta.abortmission.core;

import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.github.nagyesta.abortmission.core.MissionControl.annotationContextEvaluator;

/**
 * Abstract class providing a basic template for launch.
 */
public abstract class AbstractLaunchSequenceTemplate {

    private final Function<Class<?>, Set<MissionHealthCheckEvaluator>> classBasedEvaluatorLookup;
    private final Runnable abortSequence;

    /**
     * Constructor setting the common behavior.
     *
     * @param abortSequence             The {@link Runnable} which aborts the tests (usually by throwing an exception)
     * @param classBasedEvaluatorLookup The {@link Function} that will be used for evaluator lookup when the tested component is a class.
     */
    protected AbstractLaunchSequenceTemplate(final Runnable abortSequence,
                                             final Function<Class<?>, Set<MissionHealthCheckEvaluator>> classBasedEvaluatorLookup) {
        this.classBasedEvaluatorLookup = Objects.requireNonNull(classBasedEvaluatorLookup, "Class evaluator cannot be null.");
        this.abortSequence = Objects.requireNonNull(abortSequence, "Abort sequence cannot be null.");
    }

    /**
     * Performs pre-launch init steps, like configuring the {@link com.github.nagyesta.abortmission.core.outline.MissionOutline},
     * starting the countdown for the matching evaluators and aborting the countdown if needed.
     *
     * @param testInstanceClass The test class.
     */
    protected final void performPreLaunchInit(final Class<?> testInstanceClass) {
        annotationContextEvaluator().findAndApplyLaunchPlanDefinition(testInstanceClass);

        final Set<MissionHealthCheckEvaluator> evaluators = classBasedEvaluatorLookup.apply(testInstanceClass);
        try {
            if (!annotationContextEvaluator().isAbortSuppressed(testInstanceClass)) {
                evaluateAndAbortIfNeeded(evaluators,
                        MissionHealthCheckEvaluator::shouldAbortCountdown,
                        MissionHealthCheckEvaluator::logCountdownAborted);
            }
        } finally {
            //only log start once the abort decision was made to avoid disabling the test in all cases
            evaluators.forEach(MissionHealthCheckEvaluator::logCountdownStarted);
        }
    }

    /**
     * Marks the end of the launch preparation by marking the countdown as completed and making the abort decision for the test method
     * being started.
     *
     * @param abortSuppressionDecisionSupplier The supplier which helps us figure out whether abort decisions are suppressed.
     * @param evaluators                       The matching evaluators.
     */
    protected final void preLaunchInitComplete(final Supplier<Boolean> abortSuppressionDecisionSupplier,
                                               final Set<MissionHealthCheckEvaluator> evaluators) {
        evaluators.forEach(MissionHealthCheckEvaluator::logLaunchImminent);

        final Boolean shouldSuppressAbortDecisions = Objects.requireNonNull(abortSuppressionDecisionSupplier).get();
        if (Boolean.FALSE.equals(shouldSuppressAbortDecisions)) {
            evaluateAndAbortIfNeeded(evaluators,
                    MissionHealthCheckEvaluator::shouldAbort,
                    MissionHealthCheckEvaluator::logMissionAbort);
        }
    }

    /**
     * Handles a failure either during the launch preparation or after launch.
     *
     * @param rootCause            The exception identified as root cause for the launch failure.
     * @param evaluators           The matching evaluators.
     * @param suppressedExceptions The exceptions which must be ignored when identified as root cause.
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    protected final void failureDetected(final Optional<Throwable> rootCause,
                                         final Set<MissionHealthCheckEvaluator> evaluators,
                                         final Set<Class<? extends Exception>> suppressedExceptions) {
        if (!rootCause.isPresent()
                || suppressedExceptions.stream().noneMatch(exType -> exType.isInstance(rootCause.get()))) {
            evaluators.forEach(MissionHealthCheckEvaluator::logMissionFailure);
        }
    }

    /**
     * Marks a successful launch.
     *
     * @param evaluators The matching evaluators.
     */
    protected final void completedSuccessfully(final Set<MissionHealthCheckEvaluator> evaluators) {
        evaluators.forEach(MissionHealthCheckEvaluator::logMissionSuccess);
    }

    /**
     * Evaluates whether launch should be aborted considering the current mission status.
     *
     * @param evaluators    The matching evaluators.
     * @param abortDecision The predicate which can make the abort decision.
     * @param abortLogger   The action we want to take in order to log mission abort properly.
     */
    protected final void evaluateAndAbortIfNeeded(final Set<MissionHealthCheckEvaluator> evaluators,
                                                  final Predicate<MissionHealthCheckEvaluator> abortDecision,
                                                  final Consumer<MissionHealthCheckEvaluator> abortLogger) {
        final Set<MissionHealthCheckEvaluator> shouldAbort = evaluators.stream()
                .filter(abortDecision)
                .collect(Collectors.toSet());
        if (!shouldAbort.isEmpty()) {
            shouldAbort.forEach(abortLogger);
            abortSequence.run();
        }
    }
}
