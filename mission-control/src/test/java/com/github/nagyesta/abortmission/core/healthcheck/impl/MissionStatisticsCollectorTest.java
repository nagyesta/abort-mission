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

    @SuppressWarnings("checkstyle:MagicNumber")
    private static Stream<Arguments> equalsAndHashCodeProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(
                        ZERO,
                        new MissionStatisticsCollector(1, 0, 0, 0, 0, 0),
                        false))
                .add(Arguments.of(
                        ZERO,
                        new MissionStatisticsCollector(0, 1, 0, 0, 0, 0),
                        false))
                .add(Arguments.of(
                        ZERO,
                        new MissionStatisticsCollector(0, 0, 1, 0, 0, 0),
                        false))
                .add(Arguments.of(
                        ZERO,
                        new MissionStatisticsCollector(0, 0, 0, 1, 0, 0),
                        false))
                .add(Arguments.of(
                        ZERO,
                        new MissionStatisticsCollector(0, 0, 0, 0, 1, 0),
                        false))
                .add(Arguments.of(
                        ZERO,
                        new MissionStatisticsCollector(0, 0, 0, 0, 0, 1),
                        false))
                .add(Arguments.of(
                        new MissionStatisticsCollector(1, 2, 3, 4, 5, 6),
                        new MissionStatisticsCollector(1, 2, 3, 4, 5, 6),
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
        void testIncrementCountdownStartedShouldIncreaseCountWhenCalledNTimes(final int callCount) {
            //given
            assertEquals(0, underTest.getMissionAbort());

            //when
            IntStream.range(0, callCount).parallel().forEach(i -> underTest.incrementCountdownStarted());

            //then
            assertEquals(callCount, underTest.getCountdownStarted());
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 5, 42})
        void testIncrementCountdownCompletedShouldIncreaseCountWhenCalledNTimes(final int callCount) {
            //given
            assertEquals(0, underTest.getMissionAbort());

            //when
            IntStream.range(0, callCount).parallel().forEach(i -> underTest.incrementCountdownCompleted());

            //then
            assertEquals(callCount, underTest.getCountdownCompleted());
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 5, 42})
        void testIncrementCountdownAbortedShouldIncreaseCountWhenCalledNTimes(final int callCount) {
            //given
            assertEquals(0, underTest.getMissionAbort());

            //when
            IntStream.range(0, callCount).parallel().forEach(i -> underTest.incrementCountdownAborted());

            //then
            assertEquals(callCount, underTest.getCountdownAborted());
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 5, 42})
        void testIncrementMissionSuccessShouldIncreaseCountWhenCalledNTimes(final int callCount) {
            //given
            assertEquals(0, underTest.getMissionAbort());

            //when
            IntStream.range(0, callCount).parallel().forEach(i -> underTest.incrementMissionSuccess());

            //then
            assertEquals(callCount, underTest.getMissionSuccess());
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 5, 42})
        void testIncrementMissionFailureShouldIncreaseCountWhenCalledNTimes(final int callCount) {
            //given
            assertEquals(0, underTest.getMissionAbort());

            //when
            IntStream.range(0, callCount).parallel().forEach(i -> underTest.incrementMissionFailure());

            //then
            assertEquals(callCount, underTest.getMissionFailure());
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 5, 42})
        void testIncrementMissionAbortShouldIncreaseCountWhenCalledNTimes(final int callCount) {
            //given
            assertEquals(0, underTest.getMissionAbort());

            //when
            IntStream.range(0, callCount).parallel().forEach(i -> underTest.incrementMissionAbort());

            //then
            assertEquals(callCount, underTest.getMissionAbort());
        }
    }
}
