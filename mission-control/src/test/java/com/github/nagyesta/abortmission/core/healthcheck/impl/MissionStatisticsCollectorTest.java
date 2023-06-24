package com.github.nagyesta.abortmission.core.healthcheck.impl;

import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;
import com.github.nagyesta.abortmission.core.telemetry.StageResult;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurementBuilder;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class MissionStatisticsCollectorTest {

    private static final MissionHealthCheckMatcher MATCHER = mock(MissionHealthCheckMatcher.class);
    private static final MissionStatisticsCollector ZERO = new MissionStatisticsCollector(MATCHER);
    private static final StageStatisticsCollector STATS_ONE_ABORTED = new StageStatisticsCollector(MATCHER, 0, 1, 0, 0);
    private static final StageStatisticsCollector STATS_ONE_SUCCEEDED = new StageStatisticsCollector(MATCHER, 0, 0, 1, 0);
    private static final StageStatisticsCollector STATS_ONE_SUPPRESSED = new StageStatisticsCollector(MATCHER, 0, 0, 0, 1);
    private static final StageStatisticsCollector STATS_ONE_FAILED = new StageStatisticsCollector(MATCHER, 1, 0, 0, 0);
    private static final StageStatisticsCollector STATS_ZERO = new StageStatisticsCollector(MATCHER, 0, 0, 0, 0);

    @SuppressWarnings("checkstyle:MagicNumber")
    private static Stream<Arguments> equalsAndHashCodeProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(
                        ZERO,
                        new MissionStatisticsCollector(STATS_ONE_FAILED, STATS_ZERO),
                        false))
                .add(Arguments.of(
                        ZERO,
                        new MissionStatisticsCollector(STATS_ONE_ABORTED, STATS_ZERO),
                        false))
                .add(Arguments.of(
                        ZERO,
                        new MissionStatisticsCollector(STATS_ONE_SUCCEEDED, STATS_ZERO),
                        false))
                .add(Arguments.of(
                        ZERO,
                        new MissionStatisticsCollector(STATS_ONE_SUPPRESSED, STATS_ZERO),
                        false))
                .add(Arguments.of(
                        ZERO,
                        new MissionStatisticsCollector(STATS_ZERO, STATS_ONE_FAILED),
                        false))
                .add(Arguments.of(
                        ZERO,
                        new MissionStatisticsCollector(STATS_ZERO, STATS_ONE_SUCCEEDED),
                        false))
                .add(Arguments.of(
                        new MissionStatisticsCollector(
                                new StageStatisticsCollector(MATCHER, 1, 2, 3, 4),
                                new StageStatisticsCollector(MATCHER, 5, 6, 7, 8)),
                        new MissionStatisticsCollector(
                                new StageStatisticsCollector(MATCHER, 1, 2, 3, 4),
                                new StageStatisticsCollector(MATCHER, 5, 6, 7, 8)),
                        true))
                .add(Arguments.of(
                        ZERO,
                        null,
                        false))
                .add(Arguments.of(
                        ZERO,
                        ZERO,
                        true))
                .build();
    }

    @ParameterizedTest
    @MethodSource("equalsAndHashCodeProvider")
    void testEqualsAndHashCodeShouldBehaveInSyncWhenCalled(final MissionStatisticsCollector a,
                                                           final MissionStatisticsCollector b,
                                                           final boolean expected) {
        //given

        //when
        final boolean actual = a.equals(b);
        final int actualHashA = Objects.hashCode(a);
        final int actualHashB = Objects.hashCode(b);

        //then
        assertEquals(expected, actual);
        assertEquals(expected, actualHashA == actualHashB);
    }

    @Nested
    @SuppressWarnings("checkstyle:MagicNumber")
    class IncrementTests {

        private static final String DASH = "-";
        private final MissionStatisticsCollector underTest = new MissionStatisticsCollector(MATCHER);

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 5, 42})
        void testIncrementCountdownSucceededShouldIncreaseCountWhenCalledNTimes(final int callCount) {
            //given
            assertEquals(0, underTest.getReadOnlyCountdown().getSnapshot().getSucceeded());

            //when
            IntStream.range(0, callCount).parallel().forEach(i -> underTest.getCountdown().logAndIncrement(with(StageResult.SUCCESS)));

            //then
            assertEquals(callCount, underTest.getReadOnlyCountdown().getSnapshot().getSucceeded());
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 5, 42})
        void testIncrementCountdownAbortedShouldIncreaseCountWhenCalledNTimes(final int callCount) {
            //given
            assertEquals(0, underTest.getReadOnlyCountdown().getSnapshot().getAborted());

            //when
            IntStream.range(0, callCount).parallel().forEach(i -> underTest.getCountdown().logAndIncrement(with(StageResult.ABORT)));

            //then
            assertEquals(callCount, underTest.getReadOnlyCountdown().getSnapshot().getAborted());
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 5, 42})
        void testIncrementCountdownFailedShouldIncreaseCountWhenCalledNTimes(final int callCount) {
            //given
            assertEquals(0, underTest.getReadOnlyCountdown().getSnapshot().getFailed());

            //when
            IntStream.range(0, callCount).parallel().forEach(i -> underTest.getCountdown().logAndIncrement(with(StageResult.FAILURE)));

            //then
            assertEquals(callCount, underTest.getReadOnlyCountdown().getSnapshot().getFailed());
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 5, 42})
        void testIncrementCountdownSuppressedShouldIncreaseCountWhenCalledNTimes(final int callCount) {
            //given
            assertEquals(0, underTest.getReadOnlyCountdown().getSnapshot().getSuppressed());

            //when
            IntStream.range(0, callCount).parallel().forEach(i -> underTest.getCountdown().logAndIncrement(with(StageResult.SUPPRESSED)));

            //then
            assertEquals(callCount, underTest.getReadOnlyCountdown().getSnapshot().getSuppressed());
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 5, 42})
        void testIncrementFailureAndAbortShouldIncreaseTotalAndNotSuccessfulCountsAsWellWhenCalledNTimesEach(final int callCount) {
            //given
            assertEquals(0, underTest.getReadOnlyCountdown().getSnapshot().getTotal());
            assertEquals(0, underTest.getReadOnlyCountdown().getSnapshot().getNotSuccessful());

            //when
            IntStream.range(0, callCount).parallel().forEach(i -> underTest.getCountdown().logAndIncrement(with(StageResult.FAILURE)));
            IntStream.range(0, callCount).parallel().forEach(i -> underTest.getCountdown().logAndIncrement(with(StageResult.ABORT)));

            //then
            assertEquals(callCount * 2, underTest.getReadOnlyCountdown().getSnapshot().getTotal());
            assertEquals(callCount * 2, underTest.getReadOnlyCountdown().getSnapshot().getNotSuccessful());
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 5, 42})
        void testIncrementMissionSucceededShouldIncreaseCountWhenCalledNTimes(final int callCount) {
            //given
            assertEquals(0, underTest.getReadOnlyMission().getSnapshot().getSucceeded());

            //when
            IntStream.range(0, callCount).parallel().forEach(i -> underTest.getMission().logAndIncrement(with(StageResult.SUCCESS)));

            //then
            assertEquals(callCount, underTest.getReadOnlyMission().getSnapshot().getSucceeded());
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 5, 42})
        void testIncrementMissionAbortedShouldIncreaseCountWhenCalledNTimes(final int callCount) {
            //given
            assertEquals(0, underTest.getReadOnlyMission().getSnapshot().getAborted());

            //when
            IntStream.range(0, callCount).parallel().forEach(i -> underTest.getMission().logAndIncrement(with(StageResult.ABORT)));

            //then
            assertEquals(callCount, underTest.getReadOnlyMission().getSnapshot().getAborted());
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 5, 42})
        void testIncrementMissionFailedShouldIncreaseCountWhenCalledNTimes(final int callCount) {
            //given
            assertEquals(0, underTest.getReadOnlyMission().getSnapshot().getFailed());

            //when
            IntStream.range(0, callCount).parallel().forEach(i -> underTest.getMission().logAndIncrement(with(StageResult.FAILURE)));

            //then
            assertEquals(callCount, underTest.getReadOnlyMission().getSnapshot().getFailed());
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 5, 42})
        void testIncrementMissionSuppressedShouldIncreaseCountWhenCalledNTimes(final int callCount) {
            //given
            assertEquals(0, underTest.getReadOnlyMission().getSnapshot().getSuppressed());

            //when
            IntStream.range(0, callCount).parallel().forEach(i -> underTest.getMission().logAndIncrement(with(StageResult.SUPPRESSED)));

            //then
            assertEquals(callCount, underTest.getReadOnlyMission().getSnapshot().getSuppressed());
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 5, 42})
        void testIncrementFailureAndSuccessShouldBothIncreaseTotalAndFailureShouldIncreaseNotSuccessfulWhenCalledNTimesEach(
                final int callCount) {
            //given
            assertEquals(0, underTest.getReadOnlyMission().getSnapshot().getTotal());
            assertEquals(0, underTest.getReadOnlyMission().getSnapshot().getNotSuccessful());

            //when
            IntStream.range(0, callCount).parallel().forEach(i -> underTest.getMission().logAndIncrement(with(StageResult.FAILURE)));
            IntStream.range(0, callCount).parallel().forEach(i -> underTest.getMission().logAndIncrement(with(StageResult.SUCCESS)));

            //then
            assertEquals(callCount * 2, underTest.getReadOnlyMission().getSnapshot().getTotal());
            assertEquals(callCount, underTest.getReadOnlyMission().getSnapshot().getNotSuccessful());
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
}
