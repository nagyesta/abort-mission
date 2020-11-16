package com.github.nagyesta.abortmission.core.healthcheck.impl;

import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

@SuppressWarnings("checkstyle:MagicNumber")
class PercentageBasedMissionHealthCheckEvaluatorTest {

    private static final int ABORT_IF_HALF_FAILED = 50;

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
    @MethodSource("countdownEvaluatorProvider")
    void testBurnInThresholdsAreWorkingWhenPreparationStepsAreUsed(final int burnInCount,
                                                                   final int countdownFailure,
                                                                   final int countdownComplete,
                                                                   final int failureCount,
                                                                   final boolean expectedCountdownAbort) {
        //given
        final MissionHealthCheckMatcher anyClass = mock(MissionHealthCheckMatcher.class);
        final PercentageBasedMissionHealthCheckEvaluator underTest = PercentageBasedMissionHealthCheckEvaluator.builder(anyClass)
                .abortThreshold(1)
                .burnInTestCount(burnInCount)
                .build();

        //when
        IntStream.range(0, countdownFailure).parallel().forEach(i -> underTest.countdownLogger().incrementFailed());
        IntStream.range(0, failureCount).parallel().forEach(i -> underTest.missionLogger().incrementFailed());
        IntStream.range(0, countdownComplete).parallel().forEach(i -> underTest.countdownLogger().incrementSucceeded());
        final boolean actual = underTest.shouldAbortCountdown();

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
        final MissionHealthCheckMatcher anyClass = mock(MissionHealthCheckMatcher.class);
        final PercentageBasedMissionHealthCheckEvaluator underTest = PercentageBasedMissionHealthCheckEvaluator.builder(anyClass)
                .abortThreshold(ABORT_IF_HALF_FAILED)
                .burnInTestCount(burnInCount)
                .build();

        //when
        IntStream.range(0, failureCount).parallel().forEach(i -> underTest.missionLogger().incrementFailed());
        IntStream.range(0, successCount).parallel().forEach(i -> underTest.missionLogger().incrementSucceeded());
        IntStream.range(0, countdownComplete).parallel().forEach(i -> underTest.countdownLogger().incrementSucceeded());
        final boolean actual = underTest.shouldAbort();

        //then
        assertEquals(expectedCountdownAbort, actual);
        assertEquals(burnInCount, underTest.getBurnInTestCount());
        assertEquals(ABORT_IF_HALF_FAILED, underTest.getAbortThreshold());
    }

    @ParameterizedTest
    @ValueSource(ints = {-2, -1, 100, 101})
    void testAbortThresholdShouldThrowExceptionWhenCalledWithInvalidValue(final int input) {
        //given

        //when
        assertThrows(IllegalArgumentException.class, () -> PercentageBasedMissionHealthCheckEvaluator
                .builder(mock(MissionHealthCheckMatcher.class))
                .abortThreshold(input));

        //then exception
    }

    @ParameterizedTest
    @ValueSource(ints = {-2, -1})
    void testBurnInTestCountShouldThrowExceptionWhenCalledWithInvalidValue(final int input) {
        //given

        //when
        assertThrows(IllegalArgumentException.class, () -> PercentageBasedMissionHealthCheckEvaluator
                .builder(mock(MissionHealthCheckMatcher.class))
                .burnInTestCount(input));

        //then exception
    }
}
