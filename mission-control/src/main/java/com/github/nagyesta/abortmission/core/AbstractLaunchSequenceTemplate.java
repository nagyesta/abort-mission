package com.github.nagyesta.abortmission.core;

import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;
import com.github.nagyesta.abortmission.core.healthcheck.StatisticsLogger;
import com.github.nagyesta.abortmission.core.telemetry.StageResult;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import com.github.nagyesta.abortmission.core.telemetry.watch.StageTimeStopwatch;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.github.nagyesta.abortmission.core.MissionControl.annotationContextEvaluator;
import static com.github.nagyesta.abortmission.core.telemetry.StageResult.*;

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
     * @return A stageTimeStopwatch started to measure execution times (won't be present if reporting already happened).
     */
    protected Optional<StageTimeStopwatch> performPreLaunchInit(final Class<?> testInstanceClass) {
        annotationContextEvaluator().findAndApplyLaunchPlanDefinition(testInstanceClass);

        final StageTimeStopwatch watch = new StageTimeStopwatch(testInstanceClass);
        final Set<MissionHealthCheckEvaluator> evaluators = classBasedEvaluatorLookup.apply(testInstanceClass);
        final boolean reportingDone = evaluateAndAbortIfNeeded(
                partitionBy(evaluators, MissionHealthCheckEvaluator::shouldAbortCountdown),
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
     * @param rootCause            The exception identified as root cause for the launch failure.
     * @param suppressedExceptions The exceptions which must be ignored when identified as root cause.
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    protected void countdownFailureDetected(final Set<MissionHealthCheckEvaluator> evaluators,
                                            final Optional<StageTimeStopwatch> stopwatch,
                                            final Optional<Throwable> rootCause,
                                            final Set<Class<? extends Exception>> suppressedExceptions) {
        failureDetected(isNotSuppressed(rootCause, suppressedExceptions), evaluators, stopwatch,
                MissionHealthCheckEvaluator::countdownLogger);
    }

    /**
     * Marks the end of the launch preparation by marking the countdown as completed.
     *
     * @param evaluators The matching evaluators.
     * @param stopwatch  Captures when did the pre-launch init start.
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    protected void countdownCompletedSuccessfully(final Set<MissionHealthCheckEvaluator> evaluators,
                                                  final Optional<StageTimeStopwatch> stopwatch) {
        completedSuccessfully(evaluators, stopwatch, MissionHealthCheckEvaluator::countdownLogger);
    }

    /**
     * Makes the abort decision for the test method being started.
     *
     * @param evaluators                       The matching evaluators.
     * @param stopwatch                        The stopwatch initialized for the test method we are executing now.
     * @param abortSuppressionDecisionSupplier The supplier which helps us figure out whether abort decisions are suppressed.
     * @return A stageTimeStopwatch started to measure execution times (won't be present if reporting already happened).
     */
    protected Optional<StageTimeStopwatch> evaluateLaunchAbort(final Set<MissionHealthCheckEvaluator> evaluators,
                                                               final StageTimeStopwatch stopwatch,
                                                               final Supplier<Boolean> abortSuppressionDecisionSupplier) {
        final Boolean shouldSuppressAbortDecisions = Objects.requireNonNull(abortSuppressionDecisionSupplier).get();
        final boolean reportingDone = evaluateAndAbortIfNeeded(
                partitionBy(evaluators, MissionHealthCheckEvaluator::shouldAbort),
                Boolean.TRUE.equals(shouldSuppressAbortDecisions),
                stopwatch.stop(),
                MissionHealthCheckEvaluator::missionLogger);
        return emptyIfTrue(reportingDone, stopwatch);
    }

    /**
     * Handles a failure after launch was initiated.
     *
     * @param evaluators           The matching evaluators.
     * @param stopwatch            Captures when did the test run start.
     * @param rootCause            The exception identified as root cause for the launch failure.
     * @param suppressedExceptions The exceptions which must be ignored when identified as root cause.
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    protected void missionFailureDetected(final Set<MissionHealthCheckEvaluator> evaluators,
                                          final Optional<StageTimeStopwatch> stopwatch,
                                          final Optional<Throwable> rootCause,
                                          final Set<Class<? extends Exception>> suppressedExceptions) {
        failureDetected(isNotSuppressed(rootCause, suppressedExceptions), evaluators, stopwatch,
                MissionHealthCheckEvaluator::missionLogger);
    }

    /**
     * Marks a successful launch.
     *
     * @param evaluators The matching evaluators.
     * @param stopwatch  Captures when did the execution start.
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    protected void missionCompletedSuccessfully(final Set<MissionHealthCheckEvaluator> evaluators,
                                                final Optional<StageTimeStopwatch> stopwatch) {
        completedSuccessfully(evaluators, stopwatch, MissionHealthCheckEvaluator::missionLogger);
    }

    /**
     * Provides access to the class based lookup.
     *
     * @return Class based lookup.
     */
    protected Function<Class<?>, Set<MissionHealthCheckEvaluator>> classBasedEvaluatorLookup() {
        return classBasedEvaluatorLookup;
    }

    /**
     * Makes the abort decision for the test class/method.
     *
     * @param partitionsByShouldAbort The matching evaluators partitioned by their abort decisions.
     * @param isAbortSuppressed       The abort is suppressed by the test class or method.
     * @param timed                   The stopwatch initialized for the test method we are executing now.
     * @param loggerFunction          The function we are using to obtain the logger.
     * @return true if the reporting is already taken care of.
     */
    protected boolean evaluateAndAbortIfNeeded(final Map<Boolean, List<MissionHealthCheckEvaluator>> partitionsByShouldAbort,
                                               final Boolean isAbortSuppressed,
                                               final Function<StageResult, StageTimeMeasurement> timed,
                                               final Function<MissionHealthCheckEvaluator, StatisticsLogger> loggerFunction) {
        final List<MissionHealthCheckEvaluator> shouldAbort = partitionsByShouldAbort.getOrDefault(true, Collections.emptyList());
        final List<MissionHealthCheckEvaluator> shouldNotAbort = partitionsByShouldAbort.getOrDefault(false, Collections.emptyList());
        final boolean hasAnyAbort = !shouldAbort.isEmpty();
        if (hasAnyAbort && Boolean.TRUE.equals(isAbortSuppressed)) {
            partitionsByShouldAbort.values().forEach(list -> list.forEach(
                    logOutcomeAndIncreaseCounter(loggerFunction, timed.apply(SUPPRESSED))
            ));
            return true;
        } else if (hasAnyAbort) {
            shouldNotAbort.forEach(logOutcomeAndIncreaseCounter(loggerFunction, timed.apply(SUPPRESSED)));
            shouldAbort.forEach(logOutcomeAndIncreaseCounter(loggerFunction, timed.apply(ABORT)));
            abortSequence.run();
        }
        return false;
    }

    private Map<Boolean, List<MissionHealthCheckEvaluator>> partitionBy(final Set<MissionHealthCheckEvaluator> evaluators,
                                                                        final Predicate<MissionHealthCheckEvaluator> abortDecision) {
        return evaluators.stream().collect(Collectors.partitioningBy(abortDecision));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private boolean isNotSuppressed(final Optional<Throwable> rootCause,
                                    final Set<Class<? extends Exception>> suppressedExceptions) {
        return !rootCause.isPresent()
                || suppressedExceptions.stream().noneMatch(exType -> exType.isInstance(rootCause.get()));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private void failureDetected(final boolean isNotSuppressed,
                                 final Set<MissionHealthCheckEvaluator> evaluators,
                                 final Optional<StageTimeStopwatch> stopwatch,
                                 final Function<MissionHealthCheckEvaluator, StatisticsLogger> loggerFunction) {
        stopwatch.ifPresent(stageTimeStopwatch -> {
            if (isNotSuppressed) {
                evaluators.forEach(logOutcomeAndIncreaseCounter(loggerFunction, stageTimeStopwatch.stop().apply(FAILURE)));
            } else {
                evaluators.forEach(logOutcomeAndIncreaseCounter(loggerFunction, stageTimeStopwatch.stop().apply(SUPPRESSED)));
            }
        });
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private void completedSuccessfully(final Set<MissionHealthCheckEvaluator> evaluators,
                                       final Optional<StageTimeStopwatch> stopwatch,
                                       final Function<MissionHealthCheckEvaluator, StatisticsLogger> loggerFunction) {
        stopwatch.map(StageTimeStopwatch::stop)
                .ifPresent(watch -> evaluators.forEach(logOutcomeAndIncreaseCounter(loggerFunction, watch.apply(SUCCESS))));
    }

    private Consumer<MissionHealthCheckEvaluator> logOutcomeAndIncreaseCounter(
            final Function<MissionHealthCheckEvaluator, StatisticsLogger> loggerFunction,
            final StageTimeMeasurement measurement) {
        return e -> loggerFunction.apply(e).logAndIncrement(measurement);
    }

    private Optional<StageTimeStopwatch> emptyIfTrue(final boolean reportingDone, final StageTimeStopwatch stopwatch) {
        if (reportingDone) {
            return Optional.empty();
        } else {
            return Optional.of(stopwatch);
        }
    }
}
