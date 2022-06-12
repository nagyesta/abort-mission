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

import static com.github.nagyesta.abortmission.core.telemetry.StageResult.*;

/**
 * Abstract class providing a basic template for mission launch support.
 */
public class AbstractMissionLaunchSequenceTemplate {
    private final Runnable abortSequence;

    public AbstractMissionLaunchSequenceTemplate(final Runnable abortSequence) {
        this.abortSequence = Objects.requireNonNull(abortSequence, "Abort sequence cannot be null.");
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
        final boolean hasEvaluatorThatSuppressesAbort = evaluators.stream().anyMatch(MissionHealthCheckEvaluator::shouldSuppressAbort);
        final boolean shouldSuppressAbortDecisions = Boolean.TRUE.equals(Objects.requireNonNull(abortSuppressionDecisionSupplier).get());
        final boolean reportingDone = evaluateAndAbortIfNeeded(
                partitionBy(evaluators, missionHealthCheckEvaluator
                        -> !hasEvaluatorThatSuppressesAbort && missionHealthCheckEvaluator.shouldAbort()),
                shouldSuppressAbortDecisions,
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

    protected final Map<Boolean, List<MissionHealthCheckEvaluator>> partitionBy(
            final Set<MissionHealthCheckEvaluator> evaluators,
            final Predicate<MissionHealthCheckEvaluator> abortDecision) {
        return evaluators.stream().collect(Collectors.partitioningBy(abortDecision));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    protected final boolean isNotSuppressed(final Optional<Throwable> rootCause,
                                            final Set<Class<? extends Exception>> suppressedExceptions) {
        return rootCause.isEmpty()
                || suppressedExceptions.stream().noneMatch(exType -> exType.isInstance(rootCause.get()));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    protected final void failureDetected(final boolean isNotSuppressed,
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
    protected final void completedSuccessfully(final Set<MissionHealthCheckEvaluator> evaluators,
                                               final Optional<StageTimeStopwatch> stopwatch,
                                               final Function<MissionHealthCheckEvaluator, StatisticsLogger> loggerFunction) {
        stopwatch.map(StageTimeStopwatch::stop)
                .ifPresent(watch -> evaluators.forEach(logOutcomeAndIncreaseCounter(loggerFunction, watch.apply(SUCCESS))));
    }

    protected final Optional<StageTimeStopwatch> emptyIfTrue(final boolean reportingDone, final StageTimeStopwatch stopwatch) {
        if (reportingDone) {
            return Optional.empty();
        } else {
            return Optional.of(stopwatch);
        }
    }

    private Consumer<MissionHealthCheckEvaluator> logOutcomeAndIncreaseCounter(
            final Function<MissionHealthCheckEvaluator, StatisticsLogger> loggerFunction,
            final StageTimeMeasurement measurement) {
        return e -> loggerFunction.apply(e).logAndIncrement(measurement);
    }
}
