package com.github.nagyesta.abortmission.core.telemetry.stats;

import com.github.nagyesta.abortmission.core.telemetry.StageResult;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurementBuilder.builder;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("checkstyle:MagicNumber")
class TestRunTelemetryTest {

    private static final StageTimeMeasurement MEASUREMENT_1 =
            builder().setLaunchId(UUID.randomUUID())
                    .setTestClassId("class")
                    .setTestCaseId("method")
                    .setResult(StageResult.ABORT)
                    .setStart(0)
                    .setEnd(1)
                    .build();
    private static final StageTimeMeasurement MEASUREMENT_2 =
            builder().setLaunchId(UUID.randomUUID())
                    .setTestClassId("class")
                    .setTestCaseId("method")
                    .setResult(StageResult.ABORT)
                    .setStart(2)
                    .setEnd(4)
                    .build();

    public static Stream<Arguments> equalsProvider() {
        final var testRunTelemetry = new TestRunTelemetry(MEASUREMENT_1);
        return Stream.<Arguments>builder()
                .add(Arguments.of(testRunTelemetry, testRunTelemetry, true))
                .add(Arguments.of(testRunTelemetry, new TestRunTelemetry(MEASUREMENT_1), true))
                .add(Arguments.of(testRunTelemetry, new TestRunTelemetry(MEASUREMENT_2), false))
                .add(Arguments.of(testRunTelemetry, null, false))
                .build();
    }

    @Test
    void getDurationMillisShouldReturnTheValueSet() {
        //given
        final var underTest = new TestRunTelemetry(MEASUREMENT_1);

        //when
        final var actual = underTest.getDurationMillis();

        //then
        assertEquals(MEASUREMENT_1.getDurationMillis(), actual);
    }

    @ParameterizedTest
    @MethodSource("equalsProvider")
    void testEqualsShouldReturnTrueWhenObjectsAreEqual(
            final TestRunTelemetry a,
            final TestRunTelemetry b,
            final boolean expected) {
        //given

        //when
        final var actual = a.equals(b);

        //then
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("equalsProvider")
    void testHashCodeShouldReturnSameHashCodeWhenObjectsAreEqual(
            final TestRunTelemetry a,
            final TestRunTelemetry b,
            final boolean expected) {
        //given

        //when
        final int aHashCode = Optional.ofNullable(a).map(Objects::hashCode).orElse(0);
        final int bHashCode = Optional.ofNullable(b).map(Objects::hashCode).orElse(0);

        //then
        assertEquals(expected, aHashCode == bHashCode);
    }
}
