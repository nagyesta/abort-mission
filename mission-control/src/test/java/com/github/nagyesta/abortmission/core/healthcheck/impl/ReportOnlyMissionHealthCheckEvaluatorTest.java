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

import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("checkstyle:MagicNumber")
class ReportOnlyMissionHealthCheckEvaluatorTest {

    private static final String DASH = "-";

    private static Stream<Arguments> countdownEvaluatorProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(5, 5, 0))
                .add(Arguments.of(4, 0, 4))
                .add(Arguments.of(4, 2, 2))
                .add(Arguments.of(1, 0, 1))
                .add(Arguments.of(1, 0, 1))
                .build();
    }

    private static Stream<Arguments> launchEvaluatorProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(5, 0, 5))
                .add(Arguments.of(2, 1, 1))
                .add(Arguments.of(3, 0, 3))
                .add(Arguments.of(1, 1, 0))
                .add(Arguments.of(2, 1, 0))
                .build();
    }

    @ParameterizedTest
    @ValueSource(strings = {"a b", "a_Z"})
    @NullAndEmptySource
    void testBuilderShouldThrowExceptionWhenoverrideKeywordIsCalledWithInvalidData(final String input) {
        //given
        final var anyClass = mock(MissionHealthCheckMatcher.class);
        final var underTest = MissionControl.reportOnlyEvaluator(anyClass);

        //when
        Assertions.assertThrows(IllegalArgumentException.class, () -> underTest.overrideKeyword(input));

        //then + exception
    }

    @ParameterizedTest
    @MethodSource("countdownEvaluatorProvider")
    void testShouldAbortCountdownShouldAlwaysReturnFalseWhenCalled(final int countdownFailure,
                                                                   final int countdownComplete,
                                                                   final int failureCount) {
        //given
        final var anyClass = mock(MissionHealthCheckMatcher.class);
        final var underTest = MissionControl.reportOnlyEvaluator(anyClass)
                .overrideKeyword("all")
                .build();

        //when
        IntStream.range(0, countdownFailure).parallel()
                .forEach(i -> underTest.countdownLogger().logAndIncrement(with(StageResult.FAILURE)));
        IntStream.range(0, failureCount).parallel()
                .forEach(i -> underTest.missionLogger().logAndIncrement(with(StageResult.FAILURE)));
        IntStream.range(0, countdownComplete).parallel()
                .forEach(i -> underTest.countdownLogger().logAndIncrement(with(StageResult.SUCCESS)));
        final var actual = underTest.shouldAbortCountdown();

        //then
        assertFalse(actual);
    }

    @ParameterizedTest
    @MethodSource("launchEvaluatorProvider")
    void testShouldAbortShouldAlwaysReturnFalseWhenCalled(final int countdownComplete,
                                                          final int failureCount,
                                                          final int successCount) {
        //given
        final var anyClass = mock(MissionHealthCheckMatcher.class);
        final var underTest = MissionControl.reportOnlyEvaluator(anyClass).build();

        //when
        IntStream.range(0, failureCount).parallel()
                .forEach(i -> underTest.missionLogger().logAndIncrement(with(StageResult.FAILURE)));
        IntStream.range(0, successCount).parallel()
                .forEach(i -> underTest.missionLogger().logAndIncrement(with(StageResult.SUCCESS)));
        IntStream.range(0, countdownComplete).parallel()
                .forEach(i -> underTest.countdownLogger().logAndIncrement(with(StageResult.SUCCESS)));
        final var actual = underTest.shouldAbort();

        //then
        assertFalse(actual);
    }

    @Test
    void testShouldAbortShouldReturnTrueWhenForceAbortIsActivated() {
        //given
        final var keyWord = "all";
        final var anyClass = mock(MissionHealthCheckMatcher.class);
        final var underTest = spy(MissionControl.reportOnlyEvaluator(anyClass)
                .overrideKeyword(keyWord).build());
        doReturn(Set.of(keyWord)).when(underTest).evaluateOverrideList(MissionControl.ABORT_MISSION_FORCE_ABORT_EVALUATORS);

        //when
        final var actual = underTest.shouldAbort();

        //then
        assertTrue(actual);
        verify(underTest).evaluateOverrideList(MissionControl.ABORT_MISSION_FORCE_ABORT_EVALUATORS);
    }

    @Test
    void testShouldAbortCountdownShouldReturnTrueWhenForceAbortIsActivated() {
        //given
        final var keyWord = "all";
        final var anyClass = mock(MissionHealthCheckMatcher.class);
        final var underTest = spy(MissionControl.reportOnlyEvaluator(anyClass)
                .overrideKeyword(keyWord).build());
        doReturn(Set.of(keyWord)).when(underTest).evaluateOverrideList(MissionControl.ABORT_MISSION_FORCE_ABORT_EVALUATORS);

        //when
        final var actual = underTest.shouldAbortCountdown();

        //then
        assertTrue(actual);
        verify(underTest).evaluateOverrideList(MissionControl.ABORT_MISSION_FORCE_ABORT_EVALUATORS);
    }

    @Test
    void testGetBurnInTestCountShouldReturnConstantValue() {
        //given
        final var anyClass = mock(MissionHealthCheckMatcher.class);
        final var underTest = MissionControl.reportOnlyEvaluator(anyClass).build();

        //when
        final var actual = underTest.getBurnInTestCount();

        //then
        assertEquals(Integer.MAX_VALUE, actual);
    }

    private StageTimeMeasurement with(final StageResult result) {
        return StageTimeMeasurementBuilder.builder()
                .setLaunchId(UUID.randomUUID())
                .setTestClassId(DASH)
                .setTestCaseId(DASH)
                .setResult(result)
                .setStart(0)
                .setEnd(0)
                .build();
    }
}
