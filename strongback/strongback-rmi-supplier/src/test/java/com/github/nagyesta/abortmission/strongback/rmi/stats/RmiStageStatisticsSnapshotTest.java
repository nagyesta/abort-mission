package com.github.nagyesta.abortmission.strongback.rmi.stats;

import com.github.nagyesta.abortmission.core.healthcheck.StageStatisticsSnapshot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.stream.Stream;

@Tag("unit")
class RmiStageStatisticsSnapshotTest {

    private static final RmiStageStatisticsSnapshot ALL_ZERO = new RmiStageStatisticsSnapshot(0, 0, 0, 0);
    private static final RmiStageStatisticsSnapshot ONE_FAIL = new RmiStageStatisticsSnapshot(1, 0, 0, 0);
    private static final RmiStageStatisticsSnapshot ONE_SUCCESS = new RmiStageStatisticsSnapshot(0, 1, 0, 0);
    private static final RmiStageStatisticsSnapshot ONE_ABORT = new RmiStageStatisticsSnapshot(0, 0, 1, 0);
    private static final RmiStageStatisticsSnapshot ONE_SUPPRESS = new RmiStageStatisticsSnapshot(0, 0, 0, 1);

    @SuppressWarnings("checkstyle:MagicNumber")
    private static Stream<Arguments> statsProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(0, 0, 0, 0))
                .add(Arguments.of(0, 0, 0, 1))
                .add(Arguments.of(0, 0, 1, 0))
                .add(Arguments.of(0, 1, 0, 0))
                .add(Arguments.of(1, 0, 0, 0))
                .add(Arguments.of(1, 2, 3, 4))
                .add(Arguments.of(4, 3, 2, 1))
                .build();
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private static Stream<Arguments> equalsProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(ALL_ZERO, ALL_ZERO, true))
                .add(Arguments.of(ONE_FAIL, ALL_ZERO, false))
                .add(Arguments.of(ONE_SUCCESS, ALL_ZERO, false))
                .add(Arguments.of(ONE_ABORT, ALL_ZERO, false))
                .add(Arguments.of(ONE_SUPPRESS, ALL_ZERO, false))
                .add(Arguments.of(ALL_ZERO, ONE_FAIL, false))
                .add(Arguments.of(ALL_ZERO, ONE_SUCCESS, false))
                .add(Arguments.of(ALL_ZERO, ONE_ABORT, false))
                .add(Arguments.of(ALL_ZERO, ONE_SUPPRESS, false))
                .build();
    }

    @ParameterizedTest
    @MethodSource("statsProvider")
    void testConvertToStageStatisticsSnapshotShouldConvertAllValuableFieldsWhenCalled(
            final int failed, final int succeeded, final int aborted, final int suppressed) {
        //given
        final RmiStageStatisticsSnapshot underTest = new RmiStageStatisticsSnapshot(failed, succeeded, aborted, suppressed);

        //when
        final StageStatisticsSnapshot actual = underTest.toStageStatisticsSnapshot();

        //then
        Assertions.assertEquals(failed, actual.getFailed());
        Assertions.assertEquals(succeeded, actual.getSucceeded());
        Assertions.assertEquals(aborted, actual.getAborted());
        Assertions.assertEquals(suppressed, actual.getSuppressed());
    }

    @ParameterizedTest
    @MethodSource("statsProvider")
    @SuppressWarnings("LocalCanBeFinal")
    void testSerializationShouldRetainValuesAsIsWhenCalled(
            final int failed, final int succeeded, final int aborted, final int suppressed) throws Exception {
        //given
        final RmiStageStatisticsSnapshot underTest = new RmiStageStatisticsSnapshot();
        underTest.setAborted(aborted);
        underTest.setFailed(failed);
        underTest.setSucceeded(succeeded);
        underTest.setSuppressed(suppressed);

        try (PipedInputStream pipedInputStream = new PipedInputStream();
             PipedOutputStream pipedOutputStream = new PipedOutputStream(pipedInputStream);
             ObjectOutputStream outputStream = new ObjectOutputStream(pipedOutputStream);
             ObjectInputStream inputStream = new ObjectInputStream(pipedInputStream)) {

            //when
            outputStream.writeObject(underTest);
            final Object actual = inputStream.readObject();

            //then
            Assertions.assertTrue(actual instanceof RmiStageStatisticsSnapshot);
            Assertions.assertEquals(underTest, actual);
        }
    }

    @ParameterizedTest
    @MethodSource("equalsProvider")
    void testEqualsAndHashCodeShouldBehaveProperlyWhenCalled(
            final RmiStageStatisticsSnapshot a, final RmiStageStatisticsSnapshot b, final boolean equal) {
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
