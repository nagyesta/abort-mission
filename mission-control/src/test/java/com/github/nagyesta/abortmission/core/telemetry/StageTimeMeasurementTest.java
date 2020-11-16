package com.github.nagyesta.abortmission.core.telemetry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Objects;
import java.util.stream.Stream;

class StageTimeMeasurementTest {

    public static final String CLASS_NAME = "className";
    public static final String CLASS_NAME_2 = "className2";
    public static final int START = 10;
    public static final int START_2 = 12;
    public static final int END = 25;
    public static final int END_2 = 20;
    public static final StageTimeMeasurement STAGE_TIME_MEASUREMENT =
            new StageTimeMeasurement(CLASS_NAME, StageResult.SUPPRESSED, START, END);

    private static Stream<Arguments> objectPairProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(
                        STAGE_TIME_MEASUREMENT,
                        null,
                        -1
                ))
                .add(Arguments.of(
                        new StageTimeMeasurement(CLASS_NAME, StageResult.ABORT, START, END),
                        STAGE_TIME_MEASUREMENT,
                        -1
                ))
                .add(Arguments.of(
                        STAGE_TIME_MEASUREMENT,
                        STAGE_TIME_MEASUREMENT,
                        0
                ))
                .add(Arguments.of(
                        STAGE_TIME_MEASUREMENT,
                        new StageTimeMeasurement(CLASS_NAME, StageResult.SUPPRESSED, START, END),
                        0
                ))
                .add(Arguments.of(
                        STAGE_TIME_MEASUREMENT,
                        new StageTimeMeasurement(CLASS_NAME, StageResult.SUPPRESSED, START_2, END),
                        -1
                ))
                .add(Arguments.of(
                        STAGE_TIME_MEASUREMENT,
                        new StageTimeMeasurement(CLASS_NAME, StageResult.SUPPRESSED, START, END_2),
                        1
                ))
                .add(Arguments.of(
                        STAGE_TIME_MEASUREMENT,
                        new StageTimeMeasurement(CLASS_NAME_2, StageResult.SUPPRESSED, START, END),
                        -1
                ))
                .build();
    }

    @ParameterizedTest
    @MethodSource("objectPairProvider")
    void testEqualsAndCompareToAndHashCodeShouldBeInSyncWhenCalled(final StageTimeMeasurement a,
                                                                   final StageTimeMeasurement b,
                                                                   final int compareExpected) {
        //given

        //when
        final int actualCompare = a.compareTo(b);
        final boolean actualEquals = a.equals(b);
        final int actualHashA = Objects.hashCode(a);
        final int actualHashB = Objects.hashCode(b);

        //then
        Assertions.assertEquals(compareExpected, Integer.signum(actualCompare));
        Assertions.assertEquals(compareExpected == 0, actualEquals);
        Assertions.assertEquals(compareExpected == 0, actualHashA == actualHashB);
    }

    @Test
    void testDurationShouldSubtractStartFromEndWhenCalled() {
        //given
        //given
        @SuppressWarnings("UnnecessaryLocalVariable") final StageTimeMeasurement underTest = STAGE_TIME_MEASUREMENT;

        //when
        final long actual = underTest.getDurationMillis();

        //then
        Assertions.assertEquals(END - START, actual);
    }

    @Test
    void testToStringShouldContainPrimaryFieldsWhenCalled() {
        //given
        @SuppressWarnings("UnnecessaryLocalVariable") final StageTimeMeasurement underTest = STAGE_TIME_MEASUREMENT;

        //when
        final String actual = underTest.toString();

        //then
        Assertions.assertTrue(actual.contains(CLASS_NAME));
        Assertions.assertTrue(actual.contains(StageResult.SUPPRESSED.name()));
        Assertions.assertTrue(actual.contains(String.valueOf(START)));
        Assertions.assertTrue(actual.contains(String.valueOf(END)));
    }
}
