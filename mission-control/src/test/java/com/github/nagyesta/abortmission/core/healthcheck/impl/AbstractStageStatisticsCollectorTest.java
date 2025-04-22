package com.github.nagyesta.abortmission.core.healthcheck.impl;

import com.github.nagyesta.abortmission.core.MissionControl;
import com.github.nagyesta.abortmission.core.healthcheck.StageStatisticsSnapshot;
import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

class AbstractStageStatisticsCollectorTest {

    private static final MissionHealthCheckMatcher MATCHER = MissionControl.matcher().anyClass().build();
    private static final AbstractStageStatisticsCollector AN_INSTANCE = new AbstractStageStatisticsCollector(MATCHER) {
        @SuppressWarnings("java:S1186") //empty implementation (we just need the method to be there
        @Override
        public void logTimeMeasurement(final StageTimeMeasurement timeMeasurement) {
        }

        @Override
        public void logAndIncrement(final StageTimeMeasurement timeMeasurement) {
            super.logAndIncrement(timeMeasurement);
        }

        @Override
        public StageStatisticsSnapshot getSnapshot() {
            return null;
        }

        @Override
        public Stream<StageTimeMeasurement> timeSeriesStream() {
            return null;
        }
    };

    @SuppressWarnings("checkstyle:MagicNumber")
    public static Stream<Arguments> equalsProvider() {
        final var collector = new StageStatisticsCollector(MATCHER, 1, 2, 3, 4);
        return Stream.<Arguments>builder()
                .add(Arguments.of(AN_INSTANCE, AN_INSTANCE, true))
                .add(Arguments.of(AN_INSTANCE, collector, true))
                .add(Arguments.of(AN_INSTANCE, null, false))
                .build();
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    public static Stream<Arguments> hashCodeProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(AN_INSTANCE, AN_INSTANCE, true))
                .add(Arguments.of(AN_INSTANCE, null, false))
                .build();
    }

    @ParameterizedTest
    @MethodSource("equalsProvider")
    void testEqualsShouldReturnTrueWhenTheObjectsAreEqual(
            final AbstractStageStatisticsCollector a,
            final AbstractStageStatisticsCollector b,
            final boolean expected) {
        //given

        //when
        final var actual = a.equals(b);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("hashCodeProvider")
    void testHashCodeShouldReturnSameHashCodeWhenTheObjectsAreEqual(
            final AbstractStageStatisticsCollector a,
            final AbstractStageStatisticsCollector b,
            final boolean expected) {
        //given

        //when
        final int aHashCode = Optional.ofNullable(a).map(Objects::hashCode).orElse(0);
        final int bHashCode = Optional.ofNullable(b).map(Objects::hashCode).orElse(0);

        //then
        Assertions.assertEquals(expected, aHashCode == bHashCode);
    }

    @Test
    void testGetMatcherShouldReturnProvidedMatcherWhenCalled() {
        //given
        @SuppressWarnings("UnnecessaryLocalVariable") final var underTest = AN_INSTANCE;

        //when
        final var actual = underTest.getMatcher();

        //then
        Assertions.assertEquals(MATCHER, actual);
    }
}
