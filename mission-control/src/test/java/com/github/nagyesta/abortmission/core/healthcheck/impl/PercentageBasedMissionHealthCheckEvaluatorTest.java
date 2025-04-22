package com.github.nagyesta.abortmission.core.healthcheck.impl;

import com.github.nagyesta.abortmission.core.MissionControl;
import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;
import com.github.nagyesta.abortmission.core.telemetry.StageResult;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurementBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.nagyesta.abortmission.core.MissionControl.ABORT_MISSION_DISARM_COUNTDOWN;
import static com.github.nagyesta.abortmission.core.MissionControl.ABORT_MISSION_DISARM_MISSION;
import static com.github.nagyesta.abortmission.core.healthcheck.impl.PercentageBasedMissionHealthCheckEvaluator.builder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SuppressWarnings("checkstyle:MagicNumber")
class PercentageBasedMissionHealthCheckEvaluatorTest {

    private static final int ABORT_IF_HALF_FAILED = 50;
    private static final String DASH = "-";

    private static Stream<Arguments> countdownEvaluatorProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(3, 5, 5, 0, false))
                .add(Arguments.of(3, 4, 0, 4, true))
                .add(Arguments.of(3, 4, 2, 2, false))
                .add(Arguments.of(2, 1, 0, 1, false))
                .add(Arguments.of(1, 1, 0, 1, true))
                .build();
    }

    private static Stream<Arguments> launchEvaluatorProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(3, 5, 0, 5, false))
                .add(Arguments.of(1, 2, 1, 1, false))
                .add(Arguments.of(3, 3, 0, 3, false))
                .add(Arguments.of(2, 1, 1, 0, false))
                .add(Arguments.of(1, 2, 1, 0, true))
                .build();
    }

    @ParameterizedTest
    @ValueSource(strings = {"a b", "a_Z"})
    @NullAndEmptySource
    void testBuilderShouldThrowExceptionWhenoverrideKeywordIsCalledWithInvalidData(final String input) {
        //given
        final var anyClass = mock(MissionHealthCheckMatcher.class);
        final var underTest = MissionControl.percentageBasedEvaluator(anyClass);

        //when
        Assertions.assertThrows(IllegalArgumentException.class, () -> underTest.overrideKeyword(input));

        //then + exception
    }

    @ParameterizedTest
    @MethodSource("countdownEvaluatorProvider")
    void testBurnInThresholdsAreWorkingWhenPreparationStepsAreUsed(final int burnInCount,
                                                                   final int countdownFailure,
                                                                   final int countdownComplete,
                                                                   final int failureCount,
                                                                   final boolean expectedCountdownAbort) {
        //given
        final var anyClass = mock(MissionHealthCheckMatcher.class);
        final var underTest = builder(anyClass, new MissionStatisticsCollector(anyClass))
                .abortThreshold(1)
                .burnInTestCount(burnInCount)
                .overrideKeyword("any")
                .build();

        //when
        IntStream.range(0, countdownFailure).parallel().forEach(i -> underTest.countdownLogger().logAndIncrement(failure()));
        IntStream.range(0, failureCount).parallel().forEach(i -> underTest.missionLogger().logAndIncrement(failure()));
        IntStream.range(0, countdownComplete).parallel().forEach(i -> underTest.countdownLogger().logAndIncrement(success()));
        final var actual = underTest.shouldAbortCountdown();

        //then
        assertEquals(expectedCountdownAbort, actual);
        assertEquals(burnInCount, underTest.getBurnInTestCount());
    }

    @ParameterizedTest
    @MethodSource("launchEvaluatorProvider")
    void testBurnInThresholdsAreWorkingWhenNoPreparationStepsAreUsed(final int burnInCount,
                                                                     final int countdownComplete,
                                                                     final int failureCount,
                                                                     final int successCount,
                                                                     final boolean expectedCountdownAbort) {
        //given
        final var anyClass = mock(MissionHealthCheckMatcher.class);
        final var underTest = builder(anyClass, new MissionStatisticsCollector(anyClass))
                .abortThreshold(ABORT_IF_HALF_FAILED)
                .burnInTestCount(burnInCount)
                .build();

        //when
        IntStream.range(0, failureCount).parallel().forEach(i -> underTest.missionLogger().logAndIncrement(failure()));
        IntStream.range(0, successCount).parallel().forEach(i -> underTest.missionLogger().logAndIncrement(success()));
        IntStream.range(0, countdownComplete).parallel().forEach(i -> underTest.countdownLogger().logAndIncrement(success()));
        final var actual = underTest.shouldAbort();

        //then
        assertEquals(expectedCountdownAbort, actual);
        assertEquals(burnInCount, underTest.getBurnInTestCount());
        assertEquals(ABORT_IF_HALF_FAILED, underTest.getAbortThreshold());
    }

    @ParameterizedTest
    @ValueSource(ints = {-2, -1, 100, 101})
    void testAbortThresholdShouldThrowExceptionWhenCalledWithInvalidValue(final int input) {
        //given
        final var matcher = mock(MissionHealthCheckMatcher.class);
        final var builder = builder(matcher, new MissionStatisticsCollector(matcher));

        //when
        assertThrows(IllegalArgumentException.class, () -> builder
                .abortThreshold(input));

        //then exception
    }

    @ParameterizedTest
    @ValueSource(ints = {-2, -1})
    void testBurnInTestCountShouldThrowExceptionWhenCalledWithInvalidValue(final int input) {
        //given
        final var matcher = mock(MissionHealthCheckMatcher.class);
        final var builder = builder(matcher, new MissionStatisticsCollector(matcher));

        //when
        assertThrows(IllegalArgumentException.class, () -> builder
                .burnInTestCount(input));

        //then exception
    }

    @Test
    void testShouldAbortShouldNotCallInternalMethodWhenDisarmed() {
        //given
        final var matcher = mock(MissionHealthCheckMatcher.class);
        final var underTest = spy(builder(matcher, new MissionStatisticsCollector(matcher))
                .abortThreshold(1)
                .build());
        doReturn(true).when(underTest).isDisarmed(ABORT_MISSION_DISARM_MISSION);

        //when
        underTest.shouldAbort();

        //then
        verify(underTest, never()).shouldAbortInternal();
    }

    @Test
    void testShouldAbortCountdownShouldNotCallInternalMethodWhenDisarmed() {
        //given
        final var matcher = mock(MissionHealthCheckMatcher.class);
        final var underTest = spy(builder(matcher, new MissionStatisticsCollector(matcher))
                .abortThreshold(1)
                .build());
        doReturn(true).when(underTest).isDisarmed(ABORT_MISSION_DISARM_COUNTDOWN);

        //when
        underTest.shouldAbortCountdown();

        //then
        verify(underTest, never()).shouldAbortCountdownInternal();
    }

    private StageTimeMeasurement failure() {
        return StageTimeMeasurementBuilder.builder()
                .setLaunchId(UUID.randomUUID())
                .setTestClassId(DASH)
                .setTestCaseId(DASH)
                .setResult(StageResult.FAILURE)
                .setStart(0)
                .setEnd(0)
                .build();
    }

    private StageTimeMeasurement success() {
        return StageTimeMeasurementBuilder.builder()
                .setLaunchId(UUID.randomUUID())
                .setTestClassId(DASH)
                .setTestCaseId(DASH)
                .setResult(StageResult.SUCCESS)
                .setStart(0)
                .setEnd(0)
                .build();
    }
}
