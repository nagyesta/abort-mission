package com.github.nagyesta.abortmission.core;

import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;
import com.github.nagyesta.abortmission.core.telemetry.watch.StageTimeStopwatch;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static com.github.nagyesta.abortmission.core.MissionControl.annotationContextEvaluator;

/**
 * Abstract class providing a basic template for launch countdown support in addition to a mission.
 */
public abstract class AbstractLaunchSequenceTemplate extends AbstractMissionLaunchSequenceTemplate {

    private final Function<Class<?>, Set<MissionHealthCheckEvaluator>> classBasedEvaluatorLookup;

    /**
     * Constructor setting the common behavior.
     *
     * @param abortSequence             The {@link Runnable} which aborts the tests (usually by throwing an exception)
     * @param classBasedEvaluatorLookup The {@link Function} that will be used for evaluator lookup when the tested component is a class.
     */
    protected AbstractLaunchSequenceTemplate(
            final Runnable abortSequence,
            final Function<Class<?>, Set<MissionHealthCheckEvaluator>> classBasedEvaluatorLookup) {
        super(abortSequence);
        this.classBasedEvaluatorLookup = Objects.requireNonNull(classBasedEvaluatorLookup, "Class evaluator cannot be null.");
    }

    /**
     * Performs pre-launch init steps, like configuring the {@link com.github.nagyesta.abortmission.core.outline.MissionOutline},
     * starting the countdown for the matching evaluators, and aborting the countdown if needed.
     *
     * @param testInstanceClass The test class.
     * @param displayName       The display name of the test case.
     * @return A stageTimeStopwatch started to measure execution times (won't be present if reporting already happened).
     */
    protected Optional<StageTimeStopwatch> performPreLaunchInit(
            final Class<?> testInstanceClass,
            final String displayName) {
        annotationContextEvaluator().findAndApplyLaunchPlanDefinition(testInstanceClass);

        final var watch = new StageTimeStopwatch(testInstanceClass).overrideDisplayName(displayName);
        final var evaluators = classBasedEvaluatorLookup.apply(testInstanceClass);
        final var hasSuppression = evaluators.stream().anyMatch(MissionHealthCheckEvaluator::shouldSuppressAbort);
        final var reportingDone = evaluateAndAbortIfNeeded(
                partitionBy(evaluators, missionHealthCheckEvaluator
                        -> !hasSuppression && missionHealthCheckEvaluator.shouldAbortCountdown()),
                annotationContextEvaluator().isAbortSuppressed(testInstanceClass),
                watch.stop(),
                MissionHealthCheckEvaluator::countdownLogger);
        return emptyIfTrue(reportingDone, watch);
    }

    /**
     * Handles a failure either during the launch preparation.
     *
     * @param evaluators           The matching evaluators.
     * @param stopwatch            Captures when did the pre-launch init start.
     * @param rootCause            The exception identified as the root cause for the launch failure.
     * @param suppressedExceptions The exceptions which must be ignored when identified as the root cause.
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    protected void countdownFailureDetected(
            final Set<MissionHealthCheckEvaluator> evaluators,
            final Optional<StageTimeStopwatch> stopwatch,
            final Optional<Throwable> rootCause,
            final Set<Class<? extends Exception>> suppressedExceptions) {
        failureDetected(isNotSuppressed(rootCause, suppressedExceptions), evaluators, stopwatch.map(s -> s.addThrowable(rootCause)),
                MissionHealthCheckEvaluator::countdownLogger);
    }

    /**
     * Marks the end of the launch preparation by marking the countdown as completed.
     *
     * @param evaluators The matching evaluators.
     * @param stopwatch  Captures when did the pre-launch init start.
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    protected void countdownCompletedSuccessfully(
            final Set<MissionHealthCheckEvaluator> evaluators,
            final Optional<StageTimeStopwatch> stopwatch) {
        completedSuccessfully(evaluators, stopwatch, MissionHealthCheckEvaluator::countdownLogger);
    }

    /**
     * Provides access to the class-based lookup.
     *
     * @return Class-based lookup.
     */
    protected Function<Class<?>, Set<MissionHealthCheckEvaluator>> classBasedEvaluatorLookup() {
        return classBasedEvaluatorLookup;
    }

}
