package com.github.nagyesta.abortmission.core.healthcheck.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultStageStatisticsSnapshotTest {

    @SuppressWarnings("checkstyle:MagicNumber")
    public static Stream<Arguments> equalsProvider() {
        final DefaultStageStatisticsSnapshot anInstance = new DefaultStageStatisticsSnapshot(1, 2, 3, 4);
        return Stream.<Arguments>builder()
                .add(Arguments.of(
                        new DefaultStageStatisticsSnapshot(1, 2, 3, 4),
                        new DefaultStageStatisticsSnapshot(1, 2, 3, 4),
                        true
                ))
                .add(Arguments.of(
                        new DefaultStageStatisticsSnapshot(4, 3, 2, 1),
                        new DefaultStageStatisticsSnapshot(1, 2, 3, 4),
                        false
                ))
                .add(Arguments.of(
                        new DefaultStageStatisticsSnapshot(1, 2, 3, 4),
                        new DefaultStageStatisticsSnapshot(0, 2, 3, 4),
                        false
                ))
                .add(Arguments.of(
                        new DefaultStageStatisticsSnapshot(1, 2, 3, 4),
                        new DefaultStageStatisticsSnapshot(1, 0, 3, 4),
                        false
                ))
                .add(Arguments.of(
                        new DefaultStageStatisticsSnapshot(1, 2, 3, 4),
                        new DefaultStageStatisticsSnapshot(1, 2, 0, 4),
                        false
                ))
                .add(Arguments.of(
                        new DefaultStageStatisticsSnapshot(1, 2, 3, 4),
                        new DefaultStageStatisticsSnapshot(1, 2, 3, 0),
                        false
                ))
                .add(Arguments.of(
                        new DefaultStageStatisticsSnapshot(1, 2, 3, 4),
                        null,
                        false
                ))
                .add(Arguments.of(
                        new DefaultStageStatisticsSnapshot(1, 2, 3, 4),
                        new DefaultStageStatisticsSnapshot(1, 2, 3, 4) {
                        },
                        true
                ))
                .add(Arguments.of(
                        anInstance,
                        anInstance,
                        true
                ))
                .build();
    }

    @ParameterizedTest
    @MethodSource("equalsProvider")
    void testEqualsShouldReturnTrueWhenCalledWithEqualObject(
            final DefaultStageStatisticsSnapshot a,
            final DefaultStageStatisticsSnapshot b,
            final boolean expected) {
        //given

        //when
        final boolean equals = a.equals(b);

        //then
        assertEquals(expected, equals);
    }

    @ParameterizedTest
    @MethodSource("equalsProvider")
    void testHashCodeShouldReturnSameHashCodeWhenCalledWithEqualObject(
            final DefaultStageStatisticsSnapshot a,
            final DefaultStageStatisticsSnapshot b,
            final boolean expected) {
        //given

        //when
        final int aHashCode = Optional.ofNullable(a).map(Objects::hashCode).orElse(0);
        final int bHashCode = Optional.ofNullable(b).map(Objects::hashCode).orElse(0);

        //then
        assertEquals(expected, aHashCode == bHashCode);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    void testToStringShouldContainFieldValues() {
        //given
        final DefaultStageStatisticsSnapshot underTest = new DefaultStageStatisticsSnapshot(1, 2, 3, 4);

        //when
        final String actual = underTest.toString();

        //then
        assertTrue(actual.contains("DefaultStageStatisticsSnapshot"));
        assertTrue(actual.contains("failed=1"));
        assertTrue(actual.contains("succeeded=2"));
        assertTrue(actual.contains("aborted=3"));
        assertTrue(actual.contains("suppressed=4"));
    }
}
