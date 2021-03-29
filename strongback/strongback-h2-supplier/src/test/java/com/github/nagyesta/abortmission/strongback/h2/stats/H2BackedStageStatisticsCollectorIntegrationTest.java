package com.github.nagyesta.abortmission.strongback.h2.stats;

import com.github.nagyesta.abortmission.core.healthcheck.StageStatisticsSnapshot;
import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import com.github.nagyesta.abortmission.strongback.h2.repository.LaunchStatisticsRepository;
import com.github.nagyesta.abortmission.strongback.h2.server.AbstractInMemoryDataSourceIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement.CLASS_ONLY;
import static org.mockito.Mockito.*;

@Tag("integration")
@Execution(ExecutionMode.SAME_THREAD)
class H2BackedStageStatisticsCollectorIntegrationTest extends AbstractInMemoryDataSourceIntegrationTest {

    H2BackedStageStatisticsCollectorIntegrationTest() {
        super("collector", "jdbc:h2:mem:~/collectorTest");
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private static Stream<Arguments> measurementProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(Collections.emptyList()))
                .add(Arguments.of(IntStream.rangeClosed(0, 4)
                        .mapToObj(AbstractInMemoryDataSourceIntegrationTest::generateMeasurement)
                        .collect(Collectors.toList())))
                .add(Arguments.of(IntStream.of(42)
                        .mapToObj(AbstractInMemoryDataSourceIntegrationTest::generateMeasurement)
                        .collect(Collectors.toList())))
                .add(Arguments.of(IntStream.of(34, 43, 12, 5)
                        .mapToObj(AbstractInMemoryDataSourceIntegrationTest::generateMeasurement)
                        .collect(Collectors.toList())))
                .build();
    }

    @ParameterizedTest
    @MethodSource("measurementProvider")
    void testLogAndIncrementShouldSaveMeasurementWhenCalledWithValidInput(final List<StageTimeMeasurement> measurements) {
        //given
        final boolean countdown = false;
        final MissionHealthCheckMatcher matcher = mock(MissionHealthCheckMatcher.class);
        when(matcher.getName()).thenReturn(MATCHER_PREFIX);
        final H2BackedStageStatisticsCollector underTest =
                new H2BackedStageStatisticsCollector(contextName, matcher, dataSource, countdown);

        //when
        measurements.forEach(underTest::logAndIncrement);

        //then
        verify(matcher, atLeast(measurements.size())).getName();
        final List<StageTimeMeasurement> actual = jdbi.withExtension(LaunchStatisticsRepository.class,
                dao -> dao.fetchMeasurementsFor(contextName, MATCHER_PREFIX, countdown));
        Collections.sort(actual);
        Collections.sort(measurements);
        Assertions.assertIterableEquals(measurements, actual);
    }

    @ParameterizedTest
    @MethodSource("measurementProvider")
    void testGetSnapshotShouldCountAndGroupMeasurementWhenCalled(final List<StageTimeMeasurement> measurements) {
        //given
        final MissionHealthCheckMatcher matcher = mock(MissionHealthCheckMatcher.class);
        when(matcher.getName()).thenReturn(MATCHER_PREFIX);
        final H2BackedStageStatisticsCollector underTestCountdown =
                new H2BackedStageStatisticsCollector(contextName, matcher, dataSource, true);
        final H2BackedStageStatisticsCollector underTestMission =
                new H2BackedStageStatisticsCollector(contextName, matcher, dataSource, false);
        jdbi.withExtension(LaunchStatisticsRepository.class, dao -> {
            measurements.forEach(m -> {
                final boolean countdown = m.getTestCaseId().equals(CLASS_ONLY);
                dao.insertStageTimeMeasurement(contextName, MATCHER_PREFIX, countdown, m);
            });
            return null;
        });

        //when
        final StageStatisticsSnapshot actualCountdown = underTestCountdown.getSnapshot();
        final StageStatisticsSnapshot actualMission = underTestMission.getSnapshot();

        //then
        verify(matcher, atLeastOnce()).getName();
        assertSnapshotCountersMatch(measurements, actualCountdown, actualMission);
    }

    @ParameterizedTest
    @MethodSource("measurementProvider")
    void testFetchAllShouldReturnAllSavedMeasurementsInTheRightOrderWhenCalled(final List<StageTimeMeasurement> measurements) {
        //given
        final boolean countdown = false;
        final MissionHealthCheckMatcher matcher = mock(MissionHealthCheckMatcher.class);
        when(matcher.getName()).thenReturn(MATCHER_PREFIX);
        final H2BackedStageStatisticsCollector underTest =
                new H2BackedStageStatisticsCollector(contextName, matcher, dataSource, countdown);
        jdbi.withExtension(LaunchStatisticsRepository.class, dao -> {
            measurements.forEach(m -> dao.insertStageTimeMeasurement(contextName, MATCHER_PREFIX, countdown, m));
            return null;
        });

        //when
        final List<StageTimeMeasurement> actual = underTest.doFetchAll(contextName, matcher, countdown);

        //then
        verify(matcher, atLeastOnce()).getName();
        Assertions.assertIterableEquals(measurements.stream()
                .sorted()
                .collect(Collectors.toList()), actual);
    }
}
