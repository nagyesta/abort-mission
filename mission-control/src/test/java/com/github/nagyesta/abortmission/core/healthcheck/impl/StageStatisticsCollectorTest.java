package com.github.nagyesta.abortmission.core.healthcheck.impl;

import com.github.nagyesta.abortmission.core.MissionControl;
import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

class StageStatisticsCollectorTest {

    @SuppressWarnings("checkstyle:MagicNumber")
    public static Stream<Arguments> equalsProvider() {
        final MissionHealthCheckMatcher matcher = MissionControl.matcher().anyClass().build();
        final StageStatisticsCollector anInstance = new StageStatisticsCollector(matcher, 1, 2, 3, 4);
        return Stream.<Arguments>builder()
                .add(Arguments.of(
                        anInstance,
                        anInstance,
                        true
                ))
                .add(Arguments.of(
                        anInstance,
                        new StageStatisticsCollector(matcher, 1, 2, 3, 4),
                        true
                ))
                .add(Arguments.of(
                        anInstance,
                        new StageStatisticsCollector(matcher, 0, 2, 3, 4),
                        false
                ))
                .add(Arguments.of(
                        anInstance,
                        new StageStatisticsCollector(matcher, 1, 0, 3, 4),
                        false
                ))
                .add(Arguments.of(
                        anInstance,
                        new StageStatisticsCollector(matcher, 1, 2, 0, 4),
                        false
                ))
                .add(Arguments.of(
                        anInstance,
                        new StageStatisticsCollector(matcher, 1, 2, 3, 0),
                        false
                ))
                .add(Arguments.of(
                        anInstance,
                        null,
                        false
                ))
                .build();
    }

    @ParameterizedTest
    @MethodSource("equalsProvider")
    void testEqualsShouldReturnTrueWhenTheObjectsAreEqual(
            final StageStatisticsCollector a,
            final StageStatisticsCollector b,
            final boolean expected) {
        //given

        //when
        final boolean actual = a.equals(b);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("equalsProvider")
    void testHashCodeShouldReturnSameHashCodeWhenTheObjectsAreEqual(
            final StageStatisticsCollector a,
            final StageStatisticsCollector b,
            final boolean expected) {
        //given

        //when
        final int aHashCode = Optional.ofNullable(a).map(Objects::hashCode).orElse(0);
        final int bHashCode = Optional.ofNullable(b).map(Objects::hashCode).orElse(0);

        //then
        Assertions.assertEquals(expected, aHashCode == bHashCode);
    }
}
