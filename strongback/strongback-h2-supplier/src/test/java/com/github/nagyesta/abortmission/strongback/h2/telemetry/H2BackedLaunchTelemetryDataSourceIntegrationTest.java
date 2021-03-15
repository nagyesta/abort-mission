package com.github.nagyesta.abortmission.strongback.h2.telemetry;

import com.github.nagyesta.abortmission.core.telemetry.StageResult;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import com.github.nagyesta.abortmission.core.telemetry.stats.ClassTelemetry;
import com.github.nagyesta.abortmission.core.telemetry.stats.StageLaunchStats;
import com.github.nagyesta.abortmission.core.telemetry.stats.TestRunTelemetry;
import com.github.nagyesta.abortmission.strongback.h2.repository.LaunchStatisticsRepository;
import com.github.nagyesta.abortmission.strongback.h2.server.AbstractInMemoryDataSourceIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement.CLASS_ONLY;

@Tag("integration")
@Execution(ExecutionMode.SAME_THREAD)
class H2BackedLaunchTelemetryDataSourceIntegrationTest extends AbstractInMemoryDataSourceIntegrationTest {

    H2BackedLaunchTelemetryDataSourceIntegrationTest() {
        super("telemetry", "jdbc:h2:mem:~/telemetryTest");
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
    void testFetchClassStatisticsShouldFetchStatisticsGroupedByClassWhenCalled(
            final Map<String, List<StageTimeMeasurement>> input) {
        //given
        jdbi.withExtension(LaunchStatisticsRepository.class, insertAllCallback(input));
        final H2BackedLaunchTelemetryDataSource underTest = new H2BackedLaunchTelemetryDataSource(dataSource);

        //when
        final SortedMap<String, ClassTelemetry> actual = underTest.fetchClassStatistics();

        //then
        final Map<String, StageLaunchStats> actualLaunchStatsMap = toLaunches(actual);
        final Set<String> actualMethods = new TreeSet<>(actualLaunchStatsMap.keySet());
        final List<StageTimeMeasurement> allMeasurements = fetchAllMeasurements();
        final List<String> expectedMethods = mapToMethodNames(allMeasurements);
        Assertions.assertIterableEquals(expectedMethods, actualMethods);

        expectedMethods.forEach(method -> {
            final StageLaunchStats actualLaunch = actualLaunchStatsMap.get(method);
            final List<TestRunTelemetry> expectedMeasurements = allMeasurements.stream()
                    .filter(s -> s.getTestCaseId().equals(method))
                    .map(TestRunTelemetry::new)
                    .sorted()
                    .collect(Collectors.toList());
            Assertions.assertIterableEquals(expectedMeasurements, actualLaunch.getTimeMeasurements());

            final Map<StageResult, Integer> actualResultCount = actualLaunch.getStats().getResultCount();
            final Map<StageResult, Integer> expectedResultCount = countResults(expectedMeasurements);
            Assertions.assertIterableEquals(expectedResultCount.entrySet(), actualResultCount.entrySet());
        });

        final StageLaunchStats actualCountdown = toCountdown(actual);
        final List<TestRunTelemetry> expectedCountdowns = allMeasurements.stream()
                .filter(s -> s.getTestCaseId().equals(CLASS_ONLY))
                .map(TestRunTelemetry::new)
                .sorted()
                .collect(Collectors.toList());
        Assertions.assertIterableEquals(expectedCountdowns, actualCountdown.getTimeMeasurements());

        final Map<StageResult, Integer> actualResultCount = actualCountdown.getStats().getResultCount();
        final Map<StageResult, Integer> expectedResultCount = countResults(expectedCountdowns);
        Assertions.assertIterableEquals(expectedResultCount.entrySet(), actualResultCount.entrySet());
    }

    private Map<StageResult, Integer> countResults(final List<TestRunTelemetry> expectedMeasurements) {
        return new TreeMap<>(expectedMeasurements.stream()
                .collect(Collectors.groupingBy(TestRunTelemetry::getResult))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size())));
    }

    private Map<String, StageLaunchStats> toLaunches(final SortedMap<String, ClassTelemetry> actual) {
        return actual.get(AbstractInMemoryDataSourceIntegrationTest.CLASS_NAME)
                .getLaunches();
    }

    private StageLaunchStats toCountdown(final SortedMap<String, ClassTelemetry> actual) {
        return actual.get(AbstractInMemoryDataSourceIntegrationTest.CLASS_NAME)
                .getCountdown();
    }

    private List<String> mapToMethodNames(final List<StageTimeMeasurement> allMeasurements) {
        return allMeasurements.stream()
                .map(StageTimeMeasurement::getTestCaseId)
                .filter(s -> !CLASS_ONLY.equals(s))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    private List<StageTimeMeasurement> fetchAllMeasurements() {
        return jdbi.withExtension(LaunchStatisticsRepository.class,
                dao -> dao.fetchAllMatcherNames()
                        .stream()
                        .map(dao::fetchAllMeasurementsForMatcher)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList()));
    }
}
