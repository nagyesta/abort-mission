package com.github.nagyesta.abortmission.core.healthcheck.impl;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MissionStatisticsCollectorTest {

    private static final MissionStatisticsCollector ZERO = new MissionStatisticsCollector();
    private static final StageStatisticsCollector STATS_ONE_ABORTED = new StageStatisticsCollector(0, 1, 0, 0);
    private static final StageStatisticsCollector STATS_ONE_SUCCEEDED = new StageStatisticsCollector(0, 0, 1, 0);
    private static final StageStatisticsCollector STATS_ONE_SUPPRESSED = new StageStatisticsCollector(0, 0, 0, 1);
    private static final StageStatisticsCollector STATS_ONE_FAILED = new StageStatisticsCollector(1, 0, 0, 0);
    private static final StageStatisticsCollector STATS_ZERO = new StageStatisticsCollector(0, 0, 0, 0);

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
                                new StageStatisticsCollector(1, 2, 3, 4),
                                new StageStatisticsCollector(5, 6, 7, 8)),
                        new MissionStatisticsCollector(
                                new StageStatisticsCollector(1, 2, 3, 4),
                                new StageStatisticsCollector(5, 6, 7, 8)),
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

        private final MissionStatisticsCollector underTest = new MissionStatisticsCollector();

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 5, 42})
        void testIncrementCountdownSucceededShouldIncreaseCountWhenCalledNTimes(final int callCount) {
            //given
            assertEquals(0, underTest.getReadOnlyCountdown().getSucceeded());

            //when
            IntStream.range(0, callCount).parallel().forEach(i -> underTest.getCountdown().incrementSucceeded());

            //then
            assertEquals(callCount, underTest.getReadOnlyCountdown().getSucceeded());
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 5, 42})
        void testIncrementCountdownAbortedShouldIncreaseCountWhenCalledNTimes(final int callCount) {
            //given
            assertEquals(0, underTest.getReadOnlyCountdown().getAborted());

            //when
            IntStream.range(0, callCount).parallel().forEach(i -> underTest.getCountdown().incrementAborted());

            //then
            assertEquals(callCount, underTest.getReadOnlyCountdown().getAborted());
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 5, 42})
        void testIncrementCountdownFailedShouldIncreaseCountWhenCalledNTimes(final int callCount) {
            //given
            assertEquals(0, underTest.getReadOnlyCountdown().getFailed());

            //when
            IntStream.range(0, callCount).parallel().forEach(i -> underTest.getCountdown().incrementFailed());

            //then
            assertEquals(callCount, underTest.getReadOnlyCountdown().getFailed());
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 5, 42})
        void testIncrementCountdownSuppressedShouldIncreaseCountWhenCalledNTimes(final int callCount) {
            //given
            assertEquals(0, underTest.getReadOnlyCountdown().getSuppressed());

            //when
            IntStream.range(0, callCount).parallel().forEach(i -> underTest.getCountdown().incrementSuppressed());

            //then
            assertEquals(callCount, underTest.getReadOnlyCountdown().getSuppressed());
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 5, 42})
        void testIncrementFailureAndAbortShouldIncreaseTotalAndNotSuccessfulCountsAsWellWhenCalledNTimesEach(final int callCount) {
            //given
            assertEquals(0, underTest.getReadOnlyCountdown().getTotal());
            assertEquals(0, underTest.getReadOnlyCountdown().getNotSuccessful());

            //when
            IntStream.range(0, callCount).parallel().forEach(i -> underTest.getCountdown().incrementFailed());
            IntStream.range(0, callCount).parallel().forEach(i -> underTest.getCountdown().incrementAborted());

            //then
            assertEquals(callCount * 2, underTest.getReadOnlyCountdown().getTotal());
            assertEquals(callCount * 2, underTest.getReadOnlyCountdown().getNotSuccessful());
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 5, 42})
        void testIncrementMissionSucceededShouldIncreaseCountWhenCalledNTimes(final int callCount) {
            //given
            assertEquals(0, underTest.getReadOnlyMission().getSucceeded());

            //when
            IntStream.range(0, callCount).parallel().forEach(i -> underTest.getMission().incrementSucceeded());

            //then
            assertEquals(callCount, underTest.getReadOnlyMission().getSucceeded());
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 5, 42})
        void testIncrementMissionAbortedShouldIncreaseCountWhenCalledNTimes(final int callCount) {
            //given
            assertEquals(0, underTest.getReadOnlyMission().getAborted());

            //when
            IntStream.range(0, callCount).parallel().forEach(i -> underTest.getMission().incrementAborted());

            //then
            assertEquals(callCount, underTest.getReadOnlyMission().getAborted());
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 5, 42})
        void testIncrementMissionFailedShouldIncreaseCountWhenCalledNTimes(final int callCount) {
            //given
            assertEquals(0, underTest.getReadOnlyMission().getFailed());

            //when
            IntStream.range(0, callCount).parallel().forEach(i -> underTest.getMission().incrementFailed());

            //then
            assertEquals(callCount, underTest.getReadOnlyMission().getFailed());
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 5, 42})
        void testIncrementMissionSuppressedShouldIncreaseCountWhenCalledNTimes(final int callCount) {
            //given
            assertEquals(0, underTest.getReadOnlyMission().getSuppressed());

            //when
            IntStream.range(0, callCount).parallel().forEach(i -> underTest.getMission().incrementSuppressed());

            //then
            assertEquals(callCount, underTest.getReadOnlyMission().getSuppressed());
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 5, 42})
        void testIncrementFailureAndSuccessShouldBothIncreaseTotalAndFailureShouldIncreaseNotSuccessfulWhenCalledNTimesEach(
                final int callCount) {
            //given
            assertEquals(0, underTest.getReadOnlyMission().getTotal());
            assertEquals(0, underTest.getReadOnlyMission().getNotSuccessful());

            //when
            IntStream.range(0, callCount).parallel().forEach(i -> underTest.getMission().incrementFailed());
            IntStream.range(0, callCount).parallel().forEach(i -> underTest.getMission().incrementSucceeded());

            //then
            assertEquals(callCount * 2, underTest.getReadOnlyMission().getTotal());
            assertEquals(callCount, underTest.getReadOnlyMission().getNotSuccessful());
        }
    }
}
