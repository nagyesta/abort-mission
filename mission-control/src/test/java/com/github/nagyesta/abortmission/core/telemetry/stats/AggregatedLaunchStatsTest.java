package com.github.nagyesta.abortmission.core.telemetry.stats;

import com.github.nagyesta.abortmission.core.telemetry.StageResult;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static com.github.nagyesta.abortmission.core.telemetry.StageResult.*;

class AggregatedLaunchStatsTest extends AbstractTelemetryTest {

    private static Stream<Arguments> timeSeriesProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(
                        Arrays.asList(THIRD_ABORT_5_5, THIRD_SUPPRESS_5_5, FIRST_FAIL_1_10, FIRST_PASS_1_10, SECOND_PASS_2_20),
                        new TreeSet<>(Arrays.asList(FIRST_FAIL_1_10, SECOND_PASS_2_20, THIRD_ABORT_5_5))))
                .add(Arguments.of(
                        Arrays.asList(THIRD_ABORT_5_5, FIRST_FAIL_1_10, SECOND_PASS_2_20),
                        new TreeSet<>(Arrays.asList(FIRST_FAIL_1_10, SECOND_PASS_2_20, THIRD_ABORT_5_5))))
                .add(Arguments.of(
                        Collections.singletonList(FIRST_FAIL_1_10),
                        new TreeSet<>(Collections.singletonList(FIRST_FAIL_1_10))))
                .add(Arguments.of(
                        Arrays.asList(THIRD_PASS_5_5, THIRD_ABORT_5_5, THIRD_SUPPRESS_5_5),
                        new TreeSet<>(Collections.singletonList(THIRD_ABORT_5_5))))
                .add(Arguments.of(
                        Collections.emptySet(),
                        new TreeSet<>()))
                .build();
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private static Stream<Arguments> timeSeriesConstructorInputProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(
                        new TreeSet<>(Arrays.asList(FIRST_FAIL_1_10, SECOND_PASS_2_20, THIRD_ABORT_5_5)),
                        FAILURE,
                        LOCAL_DATE_TIME_1,
                        LOCAL_DATE_TIME_20,
                        3, 0, 18, 9.0D, 27, 1, 1, 1, 0
                ))
                .add(Arguments.of(
                        new TreeSet<>(Arrays.asList(FIRST_PASS_1_10, SECOND_PASS_2_20)),
                        SUCCESS,
                        LOCAL_DATE_TIME_1,
                        LOCAL_DATE_TIME_20,
                        2, 9, 18, 13.5D, 27, 2, 0, 0, 0
                ))
                .add(Arguments.of(
                        new TreeSet<>(Arrays.asList(SECOND_PASS_2_20, THIRD_ABORT_5_5)),
                        ABORT,
                        LOCAL_DATE_TIME_2,
                        LOCAL_DATE_TIME_20,
                        2, 0, 18, 9.0D, 18, 1, 0, 1, 0
                ))
                .add(Arguments.of(
                        new TreeSet<>(Arrays.asList(FIRST_PASS_1_10, THIRD_SUPPRESS_5_5)),
                        SUPPRESSED,
                        LOCAL_DATE_TIME_1,
                        LOCAL_DATE_TIME_10,
                        2, 0, 9, 4.5D, 9, 1, 0, 0, 1
                ))
                .add(Arguments.of(
                        Collections.emptySet(),
                        SUPPRESSED,
                        null,
                        null,
                        0, null, null, null, 0, 0, 0, 0, 0
                ))
                .build();
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private static Stream<Arguments> summarizingConstructorInputProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(
                        new HashSet<>(Arrays.asList(
                                new AggregatedLaunchStats(Collections.singleton(FIRST_FAIL_1_10)),
                                new AggregatedLaunchStats(Collections.singleton(SECOND_PASS_2_20)),
                                new AggregatedLaunchStats(Collections.singleton(THIRD_ABORT_5_5))
                        )),
                        FAILURE,
                        LOCAL_DATE_TIME_1,
                        LOCAL_DATE_TIME_20,
                        3, 0, 18, 9.0D, 27, 1, 1, 1, 0
                ))
                .add(Arguments.of(
                        new HashSet<>(Arrays.asList(
                                new AggregatedLaunchStats(Collections.singleton(FIRST_PASS_1_10)),
                                new AggregatedLaunchStats(Collections.singleton(SECOND_PASS_2_20))
                        )),
                        SUCCESS,
                        LOCAL_DATE_TIME_1,
                        LOCAL_DATE_TIME_20,
                        2, 9, 18, 13.5D, 27, 2, 0, 0, 0
                ))
                .add(Arguments.of(
                        new HashSet<>(Arrays.asList(
                                new AggregatedLaunchStats(Collections.singleton(SECOND_PASS_2_20)),
                                new AggregatedLaunchStats(Collections.singleton(THIRD_ABORT_5_5))
                        )),
                        ABORT,
                        LOCAL_DATE_TIME_2,
                        LOCAL_DATE_TIME_20,
                        2, 0, 18, 9.0D, 18, 1, 0, 1, 0
                ))
                .add(Arguments.of(
                        new HashSet<>(Arrays.asList(
                                new AggregatedLaunchStats(Collections.singleton(FIRST_PASS_1_10)),
                                new AggregatedLaunchStats(Collections.singleton(THIRD_SUPPRESS_5_5))
                        )),
                        SUPPRESSED,
                        LOCAL_DATE_TIME_1,
                        LOCAL_DATE_TIME_10,
                        2, 0, 9, 4.5D, 9, 1, 0, 0, 1
                ))
                .add(Arguments.of(
                        Collections.emptySet(),
                        SUPPRESSED,
                        null,
                        null,
                        0, null, null, null, 0, 0, 0, 0, 0
                ))
                .build();
    }

    @ParameterizedTest
    @MethodSource("timeSeriesProvider")
    void testFilterShouldRemoveOnlyDuplicatesWhenCalledWithValidInput(final Collection<StageTimeMeasurement> input,
                                                                      final SortedSet<StageTimeMeasurement> expected) {
        //given

        //when
        final SortedSet<StageTimeMeasurement> actual = AggregatedLaunchStats.filter(input);

        //then
        Assertions.assertIterableEquals(expected, actual);
    }

    @Test
    void testFilterShouldThrowExceptionWhenCalledWithNull() {
        //given

        //when
        Assertions.assertThrows(NullPointerException.class, () -> AggregatedLaunchStats.filter(null));

        //then + exception
    }

    @Test
    void testConstructorsShouldThrowExceptionWhenCalledWithNull() {
        //given

        //when
        Assertions.assertThrows(NullPointerException.class, () -> new AggregatedLaunchStats((Collection<StageTimeMeasurement>) null));
        Assertions.assertThrows(NullPointerException.class, () -> new AggregatedLaunchStats((Set<AggregatedLaunchStats>) null));

        //then + exception
    }

    @SuppressWarnings("checkstyle:ParameterNumber")
    @ParameterizedTest
    @MethodSource("timeSeriesConstructorInputProvider")
    void testConstructorShouldParseTimeSeriesDataWhenCalledWithValidInput(final Collection<StageTimeMeasurement> timeSeries,
                                                                          final StageResult expectedStageResult,
                                                                          final LocalDateTime expectedStart,
                                                                          final LocalDateTime expectedEnd,
                                                                          final int expectedCount,
                                                                          final Integer expectedMin,
                                                                          final Integer expectedMax,
                                                                          final Double expectedAvg,
                                                                          final int expectedTotal,
                                                                          final int successCount,
                                                                          final int failCount,
                                                                          final int abortCount,
                                                                          final int suppressCount) {
        //given

        //when
        final AggregatedLaunchStats actual = new AggregatedLaunchStats(timeSeries);

        //then
        assertMatches(expectedStageResult, expectedStart, expectedEnd, expectedCount, expectedMin, expectedMax, expectedAvg, expectedTotal,
                successCount, failCount, abortCount, suppressCount, actual);
    }

    @SuppressWarnings("checkstyle:ParameterNumber")
    @ParameterizedTest
    @MethodSource("summarizingConstructorInputProvider")
    void testConstructorShouldSummarizeChildStatsWhenCalledWithValidInput(final Set<AggregatedLaunchStats> children,
                                                                          final StageResult expectedStageResult,
                                                                          final LocalDateTime expectedStart,
                                                                          final LocalDateTime expectedEnd,
                                                                          final int expectedCount,
                                                                          final Integer expectedMin,
                                                                          final Integer expectedMax,
                                                                          final Double expectedAvg,
                                                                          final int expectedTotal,
                                                                          final int successCount,
                                                                          final int failCount,
                                                                          final int abortCount,
                                                                          final int suppressCount) {
        //given

        //when
        final AggregatedLaunchStats actual = new AggregatedLaunchStats(children);

        //then
        assertMatches(expectedStageResult, expectedStart, expectedEnd, expectedCount, expectedMin, expectedMax, expectedAvg, expectedTotal,
                successCount, failCount, abortCount, suppressCount, actual);
    }

    @SuppressWarnings("checkstyle:ParameterNumber")
    private void assertMatches(final StageResult expectedStageResult,
                               final LocalDateTime expectedStart,
                               final LocalDateTime expectedEnd,
                               final int expectedCount,
                               final Integer expectedMin,
                               final Integer expectedMax,
                               final Double expectedAvg,
                               final int expectedTotal,
                               final int successCount,
                               final int failCount,
                               final int abortCount,
                               final int suppressCount,
                               final AggregatedLaunchStats actual) {
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expectedStageResult, actual.getWorstResult());
        Assertions.assertEquals(expectedStart, actual.getMinStart());
        Assertions.assertEquals(expectedEnd, actual.getMaxEnd());
        Assertions.assertEquals(expectedCount, actual.getCount());
        Assertions.assertEquals(expectedMin, actual.getMinDuration());
        Assertions.assertEquals(expectedMax, actual.getMaxDuration());
        Assertions.assertEquals(expectedAvg, actual.getAvgDuration());
        Assertions.assertEquals(expectedTotal, actual.getSumDuration());
        Assertions.assertEquals(successCount, actual.getResultCount().getOrDefault(SUCCESS, 0));
        Assertions.assertEquals(failCount, actual.getResultCount().getOrDefault(FAILURE, 0));
        Assertions.assertEquals(abortCount, actual.getResultCount().getOrDefault(ABORT, 0));
        Assertions.assertEquals(suppressCount, actual.getResultCount().getOrDefault(SUPPRESSED, 0));
    }
}
