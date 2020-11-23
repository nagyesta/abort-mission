package com.github.nagyesta.abortmission.core.telemetry.stats;

import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class StageLaunchStatsTest extends AbstractTelemetryTest {

    private static Stream<Arguments> nullProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, null))
                .add(Arguments.of(null, Collections.emptySet()))
                .add(Arguments.of(null, Collections.singleton(CLASS)))
                .add(Arguments.of(new TreeSet<>(), null))
                .add(Arguments.of(new TreeSet<>(Collections.singleton(FIRST_PASS_1_10)), null))
                .build();
    }

    private static Stream<Arguments> constructorProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(
                        new TreeSet<>(Collections.singleton(FIRST_PASS_1_10)),
                        Collections.singleton(CLASS)))
                .add(Arguments.of(
                        new TreeSet<>(Arrays.asList(FIRST_PASS_1_10, SECOND_PASS_2_20, THIRD_ABORT_5_5)),
                        new TreeSet<>(Arrays.asList(CLASS, METHOD))))
                .build();
    }

    @ParameterizedTest
    @MethodSource("nullProvider")
    void testConstructorShouldThrowExceptionWhenCalledWithNull(final SortedSet<StageTimeMeasurement> measurements,
                                                               final Set<String> matcherNames) {
        //given

        //when
        Assertions.assertThrows(NullPointerException.class, () -> new StageLaunchStats(measurements, matcherNames));

        //then + exception
    }

    @ParameterizedTest
    @MethodSource("constructorProvider")
    void testConstructorShouldCopyButNotChangeInputWhenCalledWithValidInput(final SortedSet<StageTimeMeasurement> measurements,
                                                                            final Set<String> matcherNames) {
        //given
        final Set<TestRunTelemetry> testRunTelemetries = measurements.stream()
                .map(TestRunTelemetry::new)
                .collect(Collectors.toCollection(TreeSet::new));

        //when
        final StageLaunchStats actual = new StageLaunchStats(measurements, matcherNames);

        //then
        Assertions.assertThrows(UnsupportedOperationException.class, () -> actual.getMatcherNames().clear());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> actual.getTimeMeasurements().clear());
        Assertions.assertIterableEquals(matcherNames, actual.getMatcherNames());
        Assertions.assertIterableEquals(testRunTelemetries, actual.getTimeMeasurements());
        Assertions.assertNotNull(actual.getStats());
    }
}
