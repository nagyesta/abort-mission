package com.github.nagyesta.abortmission.core.telemetry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import static com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurementBuilder.builder;

class StageTimeMeasurementTest {

    private static final String METHOD_NAME = "methodName";
    private static final String METHOD_NAME_2 = "methodName2";
    private static final String CLASS_NAME = "className";
    private static final String CLASS_NAME_2 = "className2";
    private static final int START = 10;
    private static final int START_2 = 12;
    private static final int END = 25;
    private static final int END_2 = 20;
    private static final UUID LAUNCH_ID = UUID.randomUUID();
    private static final StageTimeMeasurement STAGE_TIME_MEASUREMENT = builder()
                    .setLaunchId(LAUNCH_ID)
                    .setTestClassId(CLASS_NAME)
                    .setTestCaseId(METHOD_NAME)
                    .setResult(StageResult.SUPPRESSED)
                    .setStart(START)
                    .setEnd(END)
                    .build();

    private static Stream<Arguments> objectPairProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(
                        STAGE_TIME_MEASUREMENT,
                        null,
                        -1
                ))
                .add(Arguments.of(
                        builder().setLaunchId(LAUNCH_ID)
                                .setTestClassId(CLASS_NAME)
                                .setTestCaseId(METHOD_NAME)
                                .setResult(StageResult.ABORT)
                                .setStart(START)
                                .setEnd(END)
                                .build(),
                        STAGE_TIME_MEASUREMENT,
                        -1
                ))
                .add(Arguments.of(
                        builder().setLaunchId(LAUNCH_ID)
                                .setTestClassId(CLASS_NAME)
                                .setTestCaseId(METHOD_NAME_2)
                                .setResult(StageResult.SUPPRESSED)
                                .setStart(START)
                                .setEnd(END)
                                .build(),
                        STAGE_TIME_MEASUREMENT,
                        1
                ))
                .add(Arguments.of(
                        STAGE_TIME_MEASUREMENT,
                        STAGE_TIME_MEASUREMENT,
                        0
                ))
                .add(Arguments.of(
                        STAGE_TIME_MEASUREMENT,
                        builder().setLaunchId(LAUNCH_ID)
                                .setTestClassId(CLASS_NAME)
                                .setTestCaseId(METHOD_NAME)
                                .setResult(StageResult.SUPPRESSED)
                                .setStart(START)
                                .setEnd(END)
                                .build(),
                        0
                ))
                .add(Arguments.of(
                        STAGE_TIME_MEASUREMENT,
                        builder().setLaunchId(LAUNCH_ID)
                                .setTestClassId(CLASS_NAME)
                                .setTestCaseId(METHOD_NAME)
                                .setResult(StageResult.SUPPRESSED)
                                .setStart(START_2)
                                .setEnd(END)
                                .build(),
                        -1
                ))
                .add(Arguments.of(
                        STAGE_TIME_MEASUREMENT,
                        builder().setLaunchId(LAUNCH_ID)
                                .setTestClassId(CLASS_NAME)
                                .setTestCaseId(METHOD_NAME)
                                .setResult(StageResult.SUPPRESSED)
                                .setStart(START)
                                .setEnd(END_2)
                                .build(),
                        1
                ))
                .add(Arguments.of(
                        STAGE_TIME_MEASUREMENT,
                        builder().setLaunchId(LAUNCH_ID)
                                .setTestClassId(CLASS_NAME_2)
                                .setTestCaseId(METHOD_NAME)
                                .setResult(StageResult.SUPPRESSED)
                                .setStart(START)
                                .setEnd(END)
                                .build(),
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
        final var actualCompare = a.compareTo(b);
        final var actualEquals = a.equals(b);
        final var actualHashA = Objects.hashCode(a);
        final var actualHashB = Objects.hashCode(b);

        //then
        Assertions.assertEquals(compareExpected, Integer.signum(actualCompare));
        Assertions.assertEquals(compareExpected == 0, actualEquals);
        Assertions.assertEquals(compareExpected == 0, actualHashA == actualHashB);
    }

    @Test
    void testDurationShouldSubtractStartFromEndWhenCalled() {
        //given
        @SuppressWarnings("UnnecessaryLocalVariable") final var underTest = STAGE_TIME_MEASUREMENT;

        //when
        final var actual = underTest.getDurationMillis();

        //then
        Assertions.assertEquals(END - START, actual);
    }

    @Test
    void testToStringShouldContainPrimaryFieldsWhenCalled() {
        //given
        @SuppressWarnings("UnnecessaryLocalVariable") final var underTest = STAGE_TIME_MEASUREMENT;

        //when
        final var actual = underTest.toString();

        //then
        Assertions.assertTrue(actual.contains(CLASS_NAME));
        Assertions.assertTrue(actual.contains(StageResult.SUPPRESSED.name()));
        Assertions.assertTrue(actual.contains(String.valueOf(START)));
        Assertions.assertTrue(actual.contains(String.valueOf(END)));
    }
}
