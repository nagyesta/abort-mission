package com.github.nagyesta.abortmission.core.healthcheck.impl;

import com.github.nagyesta.abortmission.core.MissionControl;
import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;
import com.github.nagyesta.abortmission.core.telemetry.StageResult;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;

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
    @MethodSource("countdownEvaluatorProvider")
    void testShouldAbortCountdownShouldAlwaysReturnFalseWhenCalled(final int countdownFailure,
                                                                   final int countdownComplete,
                                                                   final int failureCount) {
        //given
        final MissionHealthCheckMatcher anyClass = mock(MissionHealthCheckMatcher.class);
        final ReportOnlyMissionHealthCheckEvaluator underTest = MissionControl.reportOnlyEvaluator(anyClass).build();

        //when
        IntStream.range(0, countdownFailure).parallel()
                .forEach(i -> underTest.countdownLogger().logAndIncrement(with(StageResult.FAILURE)));
        IntStream.range(0, failureCount).parallel()
                .forEach(i -> underTest.missionLogger().logAndIncrement(with(StageResult.FAILURE)));
        IntStream.range(0, countdownComplete).parallel()
                .forEach(i -> underTest.countdownLogger().logAndIncrement(with(StageResult.SUCCESS)));
        final boolean actual = underTest.shouldAbortCountdown();

        //then
        assertFalse(actual);
    }

    @ParameterizedTest
    @MethodSource("launchEvaluatorProvider")
    void testShouldAbortShouldAlwaysReturnFalseWhenCalled(final int countdownComplete,
                                                          final int failureCount,
                                                          final int successCount) {
        //given
        final MissionHealthCheckMatcher anyClass = mock(MissionHealthCheckMatcher.class);
        final ReportOnlyMissionHealthCheckEvaluator underTest = MissionControl.reportOnlyEvaluator(anyClass).build();

        //when
        IntStream.range(0, failureCount).parallel()
                .forEach(i -> underTest.missionLogger().logAndIncrement(with(StageResult.FAILURE)));
        IntStream.range(0, successCount).parallel()
                .forEach(i -> underTest.missionLogger().logAndIncrement(with(StageResult.SUCCESS)));
        IntStream.range(0, countdownComplete).parallel()
                .forEach(i -> underTest.countdownLogger().logAndIncrement(with(StageResult.SUCCESS)));
        final boolean actual = underTest.shouldAbort();

        //then
        assertFalse(actual);
    }

    private StageTimeMeasurement with(final StageResult result) {
        return new StageTimeMeasurement(UUID.randomUUID(), DASH, DASH, result, 0, 0);
    }
}
