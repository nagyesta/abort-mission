package com.github.nagyesta.abortmission.strongback.h2.repository;

import com.github.nagyesta.abortmission.core.healthcheck.StageStatisticsSnapshot;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import com.github.nagyesta.abortmission.strongback.h2.server.AbstractInMemoryDataSourceIntegrationTest;
import org.jdbi.v3.core.extension.ExtensionCallback;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement.CLASS_ONLY;

@Tag("integration")
@Execution(ExecutionMode.SAME_THREAD)
public class LaunchStatisticsRepositoryIntegrationTest extends AbstractInMemoryDataSourceIntegrationTest {

    private static final String CONTEXT_NAME = "repo";

    LaunchStatisticsRepositoryIntegrationTest() {
        super(CONTEXT_NAME, "jdbc:h2:mem:~/repoTest");
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
            final Map<String, List<StageTimeMeasurement>> input) {
        //given

        //when
        jdbi.withExtension(LaunchStatisticsRepository.class, insertAllCallback(input));

        //then
        final TreeSet<UUID> actual = jdbi.open()
                .createQuery("SELECT LAUNCH_ID FROM LAUNCH_STATISTICS")
                .mapTo(UUID.class)
                .stream()
                .collect(Collectors.toCollection(TreeSet::new));
        final TreeSet<UUID> expected = input.values().stream()
                .flatMap(Collection::stream)
                .map(StageTimeMeasurement::getLaunchId)
                .collect(Collectors.toCollection(TreeSet::new));
        Assertions.assertIterableEquals(expected, actual);
    }

    @Test
    void testInsertStageTimeMeasurementShouldThrowExceptionWhenCollisionFound() {
        //given
        final StageTimeMeasurement measurement = generateMeasurement(1);
        jdbi.withExtension(LaunchStatisticsRepository.class, insertSingleMeasurementCallback(MATCHER_PREFIX, measurement));

        //when
        Assertions.assertThrows(UnableToExecuteStatementException.class,
                () -> jdbi.withExtension(LaunchStatisticsRepository.class,
                        insertSingleMeasurementCallback(MATCHER_PREFIX, measurement)));

        //then exception
    }

    @ParameterizedTest
    @MethodSource("noCollisionInsertDataProvider")
    void testFetchMeasurementsForMatcherShouldFilterTheInsertedMeasurementsWhenCalledWithValidInput(
            final Map<String, List<StageTimeMeasurement>> input) {
        //given
        final String matcherName = findMatcherName(input);
        jdbi.withExtension(LaunchStatisticsRepository.class, insertAllCallback(input));

        //when
        final List<StageTimeMeasurement> actual = jdbi.withExtension(LaunchStatisticsRepository.class,
                dao -> dao.fetchMeasurementsFor(CONTEXT_NAME, matcherName, false));

        //then
        final List<StageTimeMeasurement> expected = input.get(matcherName)
                .stream()
                .filter(s -> !s.getTestCaseId().equals(CLASS_ONLY))
                .sorted()
                .collect(Collectors.toList());
        Assertions.assertIterableEquals(expected, actual);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {METHOD_PREFIX, CLASS_NAME, MATCHER_PREFIX})
    void testFetchMeasurementsForMatcherShouldReturnNoMeasurementsWhenCalledWithUnknownMatcher(
            final String lookup) {
        //given
        final StageTimeMeasurement measurement = generateMeasurement(1);
        jdbi.withExtension(LaunchStatisticsRepository.class, insertSingleMeasurementCallback(MATCHER_PREFIX + 1, measurement));

        //when
        final List<StageTimeMeasurement> actual = jdbi.withExtension(LaunchStatisticsRepository.class,
                dao -> dao.fetchMeasurementsFor(CONTEXT_NAME, lookup, false));

        //then
        Assertions.assertNotNull(actual);
        Assertions.assertIterableEquals(Collections.emptyList(), actual);
    }

    @ParameterizedTest
    @MethodSource("noCollisionInsertDataProvider")
    void testFetchAllMatcherNamesShouldReturnADistinctListOfTheInsertedMatcherNamesWhenCalled(
            final Map<String, List<StageTimeMeasurement>> input) {
        //given
        jdbi.withExtension(LaunchStatisticsRepository.class, insertAllCallback(input));

        //when
        final List<String> actual = jdbi.withExtension(LaunchStatisticsRepository.class,
                LaunchStatisticsRepository::fetchAllMatcherNames);

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
            final Map<String, List<StageTimeMeasurement>> input) {
        //given
        final String matcherName = findMatcherName(input);
        jdbi.withExtension(LaunchStatisticsRepository.class, insertAllCallback(input));

        //when
        final StageStatisticsSnapshot actualCountdown = jdbi.withExtension(LaunchStatisticsRepository.class,
                dao -> dao.getSnapshot(CONTEXT_NAME, matcherName, true));
        final StageStatisticsSnapshot actualMission = jdbi.withExtension(LaunchStatisticsRepository.class,
                dao -> dao.getSnapshot(CONTEXT_NAME, matcherName, false));

        //then
        assertSnapshotCountersMatch(input.get(matcherName), actualCountdown, actualMission);
    }

    private String findMatcherName(final Map<String, List<StageTimeMeasurement>> input) {
        return input.keySet().stream()
                .max(Comparator.naturalOrder())
                .orElseThrow(IllegalStateException::new);
    }

    private ExtensionCallback<Void, LaunchStatisticsRepository, RuntimeException> insertSingleMeasurementCallback(
            final String matcherName, final StageTimeMeasurement measurement) {
        return dao -> {
            dao.insertStageTimeMeasurement(CONTEXT_NAME, matcherName, false, measurement);
            return null;
        };
    }
}
