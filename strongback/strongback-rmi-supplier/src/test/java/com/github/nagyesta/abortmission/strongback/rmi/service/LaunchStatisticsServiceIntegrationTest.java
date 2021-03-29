package com.github.nagyesta.abortmission.strongback.rmi.service;

import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import com.github.nagyesta.abortmission.strongback.rmi.server.RmiServiceProvider;
import com.github.nagyesta.abortmission.strongback.rmi.stats.RmiStageStatisticsSnapshot;
import com.github.nagyesta.abortmission.strongback.rmi.stats.RmiStageTimeMeasurement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement.CLASS_ONLY;

@Tag("integration")
@Execution(ExecutionMode.SAME_THREAD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LaunchStatisticsServiceIntegrationTest extends AbstractInMemoryDataSourceIntegrationTest {

    private static final String CONTEXT_NAME = "repo";

    @SuppressWarnings("checkstyle:MagicNumber")
    LaunchStatisticsServiceIntegrationTest() {
        super(CONTEXT_NAME, 32234);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private static Stream<Arguments> noCollisionInsertDataProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(mapFromStream(IntStream.of(0).boxed())))
                .add(Arguments.of(mapFromStream(IntStream.of(9).boxed())))
                .add(Arguments.of(mapFromStream(IntStream.of(1, 3, 42).boxed())))
                .add(Arguments.of(mapFromStream(IntStream.rangeClosed(0, 30).boxed())))
                .add(Arguments.of(mapFromMultiStream(IntStream.of(9).boxed())))
                .add(Arguments.of(mapFromMultiStream(IntStream.of(1, 3).boxed())))
                .add(Arguments.of(mapFromMultiStream(IntStream.rangeClosed(2, 5).boxed())))
                .build();
    }

    @ParameterizedTest
    @MethodSource("noCollisionInsertDataProvider")
    void testInsertStageTimeMeasurementShouldInsertUniqueRecordsWhenCalledWithoutCollision(
            final Map<String, List<StageTimeMeasurement>> input) throws RemoteException {
        //given
        final LaunchStatisticsService underTest = RmiServiceProvider.service(RmiServiceProvider.lookupRegistry(port));

        //when
        for (final Map.Entry<String, List<StageTimeMeasurement>> entry : input.entrySet()) {
            final String key = entry.getKey();
            final List<StageTimeMeasurement> measurementList = entry.getValue();
            for (final StageTimeMeasurement measurement : measurementList) {
                final boolean isCountdown = CLASS_ONLY.equals(measurement.getTestCaseId());
                underTest.insertStageTimeMeasurement(contextName, key, isCountdown, new RmiStageTimeMeasurement(measurement));
            }
        }

        //then
        for (final Map.Entry<String, List<StageTimeMeasurement>> entry : input.entrySet()) {
            final String matcherName = entry.getKey();
            final List<StageTimeMeasurement> measurements = entry.getValue();
            final TreeSet<UUID> actual = this.service.fetchAllMeasurementsForMatcher(matcherName)
                    .stream()
                    .map(RmiStageTimeMeasurement::getLaunchId)
                    .collect(Collectors.toCollection(TreeSet::new));
            final TreeSet<UUID> expected = measurements.stream()
                    .map(StageTimeMeasurement::getLaunchId)
                    .collect(Collectors.toCollection(TreeSet::new));
            Assertions.assertIterableEquals(expected, actual);
        }
    }

    @ParameterizedTest
    @MethodSource("noCollisionInsertDataProvider")
    void testFetchMeasurementsForMatcherShouldFilterTheInsertedMeasurementsWhenCalledWithValidInput(
            final Map<String, List<StageTimeMeasurement>> input) throws RemoteException {
        //given
        final LaunchStatisticsService underTest = RmiServiceProvider.service(RmiServiceProvider.lookupRegistry(port));
        final String matcherName = findMatcherName(input);
        insertAll(input);

        //when
        final List<RmiStageTimeMeasurement> actual = underTest.fetchMeasurementsFor(CONTEXT_NAME, matcherName, false);

        //then
        final List<RmiStageTimeMeasurement> expected = input.get(matcherName)
                .stream()
                .filter(s -> !s.getTestCaseId().equals(CLASS_ONLY))
                .sorted()
                .map(RmiStageTimeMeasurement::new)
                .collect(Collectors.toList());
        Assertions.assertIterableEquals(expected, actual);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {METHOD_PREFIX, CLASS_NAME, MATCHER_PREFIX})
    void testFetchMeasurementsForMatcherShouldReturnNoMeasurementsWhenCalledWithUnknownMatcher(
            final String lookup) throws RemoteException {
        //given
        final LaunchStatisticsService underTest = RmiServiceProvider.service(RmiServiceProvider.lookupRegistry(port));
        final RmiStageTimeMeasurement measurement = new RmiStageTimeMeasurement(generateMeasurement(1));
        insertSingleMeasurement(MATCHER_PREFIX + 1, measurement);

        //when
        final List<RmiStageTimeMeasurement> actual = underTest.fetchMeasurementsFor(CONTEXT_NAME, lookup, false);

        //then
        Assertions.assertNotNull(actual);
        Assertions.assertIterableEquals(Collections.emptyList(), actual);
    }

    @ParameterizedTest
    @MethodSource("noCollisionInsertDataProvider")
    void testFetchAllMatcherNamesShouldReturnADistinctListOfTheInsertedMatcherNamesWhenCalled(
            final Map<String, List<StageTimeMeasurement>> input) throws RemoteException {
        //given
        final LaunchStatisticsService underTest = RmiServiceProvider.service(RmiServiceProvider.lookupRegistry(port));
        insertAll(input);

        //when
        final List<String> actual = underTest.fetchAllMatcherNames();

        //then
        final List<String> expected = input.keySet()
                .stream()
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        Assertions.assertIterableEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("noCollisionInsertDataProvider")
    void testGetSnapshotOfAMatcherShouldReturnTheAggregatedStatusCountsWhenCalled(
            final Map<String, List<StageTimeMeasurement>> input) throws RemoteException {
        //given
        final LaunchStatisticsService underTest = RmiServiceProvider.service(RmiServiceProvider.lookupRegistry(port));
        final String matcherName = findMatcherName(input);
        insertAll(input);

        //when
        final RmiStageStatisticsSnapshot actualCountdown = underTest.getSnapshot(CONTEXT_NAME, matcherName, true);
        final RmiStageStatisticsSnapshot actualMission = underTest.getSnapshot(CONTEXT_NAME, matcherName, false);

        //then
        assertSnapshotCountersMatch(input.get(matcherName), actualCountdown, actualMission);
    }

    private String findMatcherName(final Map<String, List<StageTimeMeasurement>> input) {
        return input.keySet().stream()
                .max(Comparator.naturalOrder())
                .orElseThrow(IllegalStateException::new);
    }

    private void insertSingleMeasurement(final String matcherName, final RmiStageTimeMeasurement measurement) throws RemoteException {
        service.insertStageTimeMeasurement(CONTEXT_NAME, matcherName, false, measurement);
    }
}
