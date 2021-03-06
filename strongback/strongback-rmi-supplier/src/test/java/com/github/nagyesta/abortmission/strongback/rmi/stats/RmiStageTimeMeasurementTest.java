package com.github.nagyesta.abortmission.strongback.rmi.stats;

import com.github.nagyesta.abortmission.core.telemetry.StageResult;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.UUID;
import java.util.stream.Stream;

@Tag("unit")
@SuppressWarnings("checkstyle:MagicNumber")
class RmiStageTimeMeasurementTest {

    private static final StageTimeMeasurement ABORTED = new StageTimeMeasurement(
            UUID.randomUUID(),
            "testClassId",
            "testCaseId",
            StageResult.ABORT,
            0,
            1);
    private static final StageTimeMeasurement FAILED = new StageTimeMeasurement(
            UUID.fromString("fa79f083-0a5c-4454-9bc3-c886f6c345be"),
            StageTimeMeasurement.class.getSimpleName(),
            StageTimeMeasurement.CLASS_ONLY,
            StageResult.FAILURE,
            10,
            21);
    private static final StageTimeMeasurement FAILED_2 = new StageTimeMeasurement(
            UUID.fromString("fa79f083-0a5c-4454-9bc3-c886f6c345be"),
            StageTimeMeasurement.class.getSimpleName(),
            StageTimeMeasurement.CLASS_ONLY,
            StageResult.FAILURE,
            10,
            22);
    private static final StageTimeMeasurement SUPPRESSED = new StageTimeMeasurement(
            UUID.fromString("fa79f083-0a5c-4454-9bc3-c886f6c345bf"),
            "class",
            "method",
            StageResult.SUPPRESSED,
            40,
            42);
    private static final StageTimeMeasurement SUPPRESSED_2 = new StageTimeMeasurement(
            UUID.fromString("fa79f083-0a5c-4454-9bc3-c886f6c345bf"),
            "class",
            "method",
            StageResult.SUPPRESSED,
            41,
            42);
    private static final StageTimeMeasurement SUPPRESSED_3 = new StageTimeMeasurement(
            UUID.fromString("fa79f083-0a5c-4454-9bc3-c886f6c345bf"),
            "class-2",
            "method",
            StageResult.SUPPRESSED,
            41,
            42);
    private static final StageTimeMeasurement SUPPRESSED_4 = new StageTimeMeasurement(
            UUID.fromString("fa79f083-0a5c-4454-9bc3-c886f6c345bf"),
            "class",
            "method-2",
            StageResult.SUPPRESSED,
            41,
            42);
    private static final StageTimeMeasurement SUCCESS = new StageTimeMeasurement(
            UUID.fromString("fa79f083-0a5c-4454-9bc3-c886f6c345bf"),
            "class",
            "method",
            StageResult.SUCCESS,
            41,
            42);

    private static Stream<Arguments> measurementProvider() {
        return Stream.<StageTimeMeasurement>builder()
                .add(ABORTED)
                .add(FAILED)
                .add(SUPPRESSED)
                .build()
                .map(Arguments::of);
    }

    private static Stream<Arguments> equalsProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(new RmiStageTimeMeasurement(ABORTED), new RmiStageTimeMeasurement(ABORTED), true))
                .add(Arguments.of(new RmiStageTimeMeasurement(FAILED), new RmiStageTimeMeasurement(ABORTED), false))
                .add(Arguments.of(new RmiStageTimeMeasurement(FAILED), new RmiStageTimeMeasurement(FAILED_2), false))
                .add(Arguments.of(new RmiStageTimeMeasurement(SUPPRESSED), new RmiStageTimeMeasurement(SUPPRESSED_2), false))
                .add(Arguments.of(new RmiStageTimeMeasurement(SUPPRESSED_3), new RmiStageTimeMeasurement(SUPPRESSED_2), false))
                .add(Arguments.of(new RmiStageTimeMeasurement(SUPPRESSED_2), new RmiStageTimeMeasurement(SUPPRESSED_4), false))
                .add(Arguments.of(new RmiStageTimeMeasurement(SUPPRESSED_2), new RmiStageTimeMeasurement(SUCCESS), false))
                .add(Arguments.of(new RmiStageTimeMeasurement(ABORTED), new RmiStageTimeMeasurement(SUPPRESSED), false))
                .add(Arguments.of(new RmiStageTimeMeasurement(SUPPRESSED), new RmiStageTimeMeasurement(FAILED), false))
                .add(Arguments.of(new RmiStageTimeMeasurement(SUPPRESSED), new RmiStageTimeMeasurement(ABORTED), false))
                .build();
    }

    @ParameterizedTest
    @MethodSource("measurementProvider")
    void testConvertToStageTimeMeasurementShouldConvertAllValuableFieldsWhenCalled(
            final StageTimeMeasurement expected) {
        //given
        final RmiStageTimeMeasurement underTest = new RmiStageTimeMeasurement(expected);

        //when
        final StageTimeMeasurement actual = underTest.toStageTimeMeasurement();

        //then
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("measurementProvider")
    @SuppressWarnings("LocalCanBeFinal")
    void testSerializationShouldRetainValuesAsIsWhenCalled(
            final StageTimeMeasurement input) throws Exception {
        //given
        final RmiStageTimeMeasurement underTest = new RmiStageTimeMeasurement();
        underTest.setStart(input.getStart());
        underTest.setEnd(input.getEnd());
        underTest.setTestCaseId(input.getTestCaseId());
        underTest.setTestClassId(input.getTestClassId());
        underTest.setResult(input.getResult());
        underTest.setLaunchId(input.getLaunchId());

        try (PipedInputStream pipedInputStream = new PipedInputStream();
             PipedOutputStream pipedOutputStream = new PipedOutputStream(pipedInputStream);
             ObjectOutputStream outputStream = new ObjectOutputStream(pipedOutputStream);
             ObjectInputStream inputStream = new ObjectInputStream(pipedInputStream)) {

            //when
            outputStream.writeObject(underTest);
            final Object actual = inputStream.readObject();

            //then
            Assertions.assertTrue(actual instanceof RmiStageTimeMeasurement);
            Assertions.assertEquals(underTest, actual);
        }
    }

    @ParameterizedTest
    @MethodSource("equalsProvider")
    void testEqualsAndHashCodeShouldBehaveProperlyWhenCalled(
            final RmiStageTimeMeasurement a, final RmiStageTimeMeasurement b, final boolean equal) {
        //given

        //when
        final boolean actualEquals = a.equals(b);
        final int hashA = a.hashCode();
        final int hashB = b.hashCode();

        //then
        Assertions.assertEquals(equal, actualEquals);
        Assertions.assertEquals(hashA == hashB, equal);
    }
}
