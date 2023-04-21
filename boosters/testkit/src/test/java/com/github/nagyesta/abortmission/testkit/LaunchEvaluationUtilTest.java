package com.github.nagyesta.abortmission.testkit;

import com.github.nagyesta.abortmission.core.MissionControl;
import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;
import com.github.nagyesta.abortmission.core.healthcheck.impl.ReportOnlyMissionHealthCheckEvaluator;
import com.github.nagyesta.abortmission.core.telemetry.StageResult;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import com.github.nagyesta.abortmission.core.telemetry.watch.StageTimeStopwatch;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

class LaunchEvaluationUtilTest {

    private static final String EXPECTED = "expected";
    private static final String EXPECTED_CLASS = IllegalStateException.class.getName();
    private static final String DISPLAY_NAME = "DISPLAY_NAME";

    @Test
    void testConstructorShouldThrowExceptionWhenCalled() throws NoSuchMethodException {
        //given
        final Constructor<LaunchEvaluationUtil> constructor = LaunchEvaluationUtil.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        //when
        Assertions.assertThrows(InvocationTargetException.class, constructor::newInstance);

        //then + exception
    }

    @Test
    void testFindExceptionsForMissionFailuresOfShouldReturnListOfExceptionClassesCollectedByMissionsWhenCalled() {
        //given
        final MissionHealthCheckEvaluator evaluator = getEvaluator();
        evaluator.missionLogger().logAndIncrement(failureMeasurement());

        //when
        final List<String> actual = LaunchEvaluationUtil.findExceptionsForMissionFailuresOf(evaluator);

        //then
        Assertions.assertIterableEquals(List.of(EXPECTED_CLASS), actual);
    }

    @Test
    void testFindThrowableMessagesForMissionFailuresOfShouldReturnListOfExceptionMessagesCollectedByMissionsWhenCalled() {
        //given
        final MissionHealthCheckEvaluator evaluator = getEvaluator();
        evaluator.missionLogger().logAndIncrement(failureMeasurement());

        //when
        final List<String> actual = LaunchEvaluationUtil.findThrowableMessagesForMissionFailuresOf(evaluator);

        //then
        Assertions.assertIterableEquals(List.of(EXPECTED), actual);
    }

    @Test
    void testFindExceptionsForCountdownFailuresOfShouldReturnListOfExceptionClassesCollectedByCountdownsWhenCalled() {
        //given
        final MissionHealthCheckEvaluator evaluator = getEvaluator();
        evaluator.countdownLogger().logAndIncrement(failureMeasurement());

        //when
        final List<String> actual = LaunchEvaluationUtil.findExceptionsForCountdownFailuresOf(evaluator);

        //then
        Assertions.assertIterableEquals(List.of(EXPECTED_CLASS), actual);
    }

    @Test
    void testFindThrowableMessagesForCountdownFailuresOfShouldReturnListOfExceptionMessagesCollectedByCountdownsWhenCalled() {
        //given
        final MissionHealthCheckEvaluator evaluator = getEvaluator();
        evaluator.countdownLogger().logAndIncrement(failureMeasurement());

        //when
        final List<String> actual = LaunchEvaluationUtil.findThrowableMessagesForCountdownFailuresOf(evaluator);

        //then
        Assertions.assertIterableEquals(List.of(EXPECTED), actual);
    }

    @Test
    void testFindCountdownDisplayNamesForMeasurementsOfShouldReturnDisplayNamesOfCountdownMeasurementsWhenCalled() {
        //given
        final MissionHealthCheckEvaluator evaluator = getEvaluator();
        evaluator.countdownLogger().logAndIncrement(failureMeasurement());

        //when
        final List<String> actual = LaunchEvaluationUtil.findCountdownDisplayNamesForMeasurementsOf(evaluator);

        //then
        Assertions.assertIterableEquals(List.of(DISPLAY_NAME), actual);
    }

    @Test
    void testFindMissionDisplayNamesForMeasurementsOfShouldReturnDisplayNamesOfMissionMeasurementsWhenCalled() {
        //given
        final MissionHealthCheckEvaluator evaluator = getEvaluator();
        evaluator.missionLogger().logAndIncrement(failureMeasurement());

        //when
        final List<String> actual = LaunchEvaluationUtil.findMissionDisplayNamesForMeasurementsOf(evaluator);

        //then
        Assertions.assertIterableEquals(List.of(DISPLAY_NAME), actual);
    }

    @Test
    void testForEachNonFilteredStackTraceElementOfMissionFailuresShouldCallConsumerForEachStackTraceElementWhenCalled() {
        //given
        final MissionHealthCheckEvaluator evaluator = getEvaluator();
        final StageTimeMeasurement measurement = failureMeasurement();
        evaluator.missionLogger().logAndIncrement(measurement);
        final List<String> expected = measurement.getStackTrace();
        final List<String> actual = new ArrayList<>();

        //when
        LaunchEvaluationUtil.forEachNonFilteredStackTraceElementOfMissionFailures(evaluator, actual::add);

        //then
        Assertions.assertIterableEquals(expected, actual);
    }

    private ReportOnlyMissionHealthCheckEvaluator getEvaluator() {
        return MissionControl.reportOnlyEvaluator(MissionControl.matcher().anyClass().build()).build();
    }

    private StageTimeMeasurement failureMeasurement() {
        final Function<StageResult, StageTimeMeasurement> failure = new StageTimeStopwatch(getClass())
                .overrideDisplayName(DISPLAY_NAME)
                .addThrowable(Optional.of(createThrowable()))
                .stop();
        return failure.apply(StageResult.FAILURE);
    }

    private static Throwable createThrowable() {
        return new IllegalStateException(EXPECTED).fillInStackTrace();
    }
}
