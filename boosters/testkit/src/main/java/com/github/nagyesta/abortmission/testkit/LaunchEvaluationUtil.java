package com.github.nagyesta.abortmission.testkit;

import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;
import com.github.nagyesta.abortmission.core.healthcheck.ReadOnlyStageStatistics;
import com.github.nagyesta.abortmission.core.telemetry.StageResult;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Utility class for helping with the evaluation of launch statistics.
 */
public final class LaunchEvaluationUtil {

    private LaunchEvaluationUtil() {
        throw new IllegalCallerException("Utility class.");
    }

    /**
     * Returns the exception classes causing the failures for the given evaluator.
     *
     * @param evaluator The evaluator we want to check.
     * @return The exception classes causing the failures.
     */
    public static List<String> findExceptionsForMissionFailuresOf(final MissionHealthCheckEvaluator evaluator) {
        final var statistics = evaluator.getStats().getReadOnlyMission();
        return findExceptions(statistics);
    }

    /**
     * Returns the exception classes causing the failures for the given evaluator.
     *
     * @param evaluator The evaluator we want to check.
     * @return The exception classes causing the failures.
     */
    public static List<String> findExceptionsForCountdownFailuresOf(final MissionHealthCheckEvaluator evaluator) {
        final var statistics = evaluator.getStats().getReadOnlyCountdown();
        return findExceptions(statistics);
    }

    /**
     * Returns the messages of the exceptions causing the failures for the given evaluator.
     *
     * @param evaluator The evaluator we want to check.
     * @return The messages of the exceptions causing the failures.
     */
    public static List<String> findThrowableMessagesForMissionFailuresOf(final MissionHealthCheckEvaluator evaluator) {
        final var statistics = evaluator.getStats().getReadOnlyMission();
        return findThrowableMessages(statistics);
    }

    /**
     * Returns the messages of the exceptions causing the failures for the given evaluator.
     *
     * @param evaluator The evaluator we want to check.
     * @return The messages of the exceptions causing the failures.
     */
    public static List<String> findThrowableMessagesForCountdownFailuresOf(final MissionHealthCheckEvaluator evaluator) {
        final var statistics = evaluator.getStats().getReadOnlyCountdown();
        return findThrowableMessages(statistics);
    }

    /**
     * Returns the display names of the countdown measurements for the given evaluator.
     *
     * @param evaluator The evaluator we want to check.
     * @return The display names of the countdown measurements.
     */
    public static List<String> findCountdownDisplayNamesForMeasurementsOf(final MissionHealthCheckEvaluator evaluator) {
        return evaluator.getStats().getReadOnlyCountdown().timeSeriesStream()
                .map(StageTimeMeasurement::getDisplayName)
                .collect(Collectors.toList());
    }

    /**
     * Returns the display names of the mission measurements for the given evaluator.
     *
     * @param evaluator The evaluator we want to check.
     * @return The display names of the mission measurements.
     */
    public static List<String> findMissionDisplayNamesForMeasurementsOf(final MissionHealthCheckEvaluator evaluator) {
        return evaluator.getStats().getReadOnlyMission().timeSeriesStream()
                .map(StageTimeMeasurement::getDisplayName)
                .collect(Collectors.toList());
    }

    /**
     * Applies the given action to the stack trace elements of the mission failures.
     *
     * @param evaluator The evaluator we want to check.
     * @param action    The action we want to apply.
     */
    public static void forEachNonFilteredStackTraceElementOfMissionFailures(
            final MissionHealthCheckEvaluator evaluator, final Consumer<String> action) {
        evaluator.getStats().getReadOnlyMission().timeSeriesStream()
                .filter(s -> s.getResult() == StageResult.FAILURE)
                .map(StageTimeMeasurement::getStackTrace)
                .flatMap(List::stream)
                .filter(e -> !e.equals("< filtered >"))
                .forEach(action);
    }

    private static List<String> findExceptions(final ReadOnlyStageStatistics statistics) {
        return statistics.timeSeriesStream()
                .filter(s -> s.getResult() == StageResult.FAILURE)
                .map(StageTimeMeasurement::getThrowableClass)
                .collect(Collectors.toList());
    }

    private static List<String> findThrowableMessages(final ReadOnlyStageStatistics statistics) {
        return statistics.timeSeriesStream()
                .filter(s -> s.getResult() == StageResult.FAILURE)
                .map(StageTimeMeasurement::getThrowableMessage)
                .collect(Collectors.toList());
    }
}
