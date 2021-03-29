package com.github.nagyesta.abortmission.strongback.rmi.stats;

import com.github.nagyesta.abortmission.core.healthcheck.StageStatisticsSnapshot;
import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import com.github.nagyesta.abortmission.strongback.rmi.server.RmiServiceProvider;
import com.github.nagyesta.abortmission.strongback.rmi.service.AbstractInMemoryDataSourceIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement.CLASS_ONLY;
import static org.mockito.Mockito.*;

@Tag("integration")
@Execution(ExecutionMode.SAME_THREAD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RmiBackedStageStatisticsCollectorIntegrationTest extends AbstractInMemoryDataSourceIntegrationTest {

    @SuppressWarnings("checkstyle:MagicNumber")
    RmiBackedStageStatisticsCollectorIntegrationTest() {
        super("collector", 30343);
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
    void testLogAndIncrementShouldSaveMeasurementWhenCalledWithValidInput(
            final List<StageTimeMeasurement> measurements) throws RemoteException {
        //given
        final Registry registry = RmiServiceProvider.lookupRegistry(port);
        final boolean countdown = false;
        final MissionHealthCheckMatcher matcher = mock(MissionHealthCheckMatcher.class);
        when(matcher.getName()).thenReturn(MATCHER_PREFIX);
        final RmiBackedStageStatisticsCollector underTest =
                new RmiBackedStageStatisticsCollector(contextName, matcher, registry, countdown);

        //when
        measurements.forEach(underTest::logAndIncrement);

        //then
        verify(matcher, atLeast(measurements.size())).getName();
        final List<RmiStageTimeMeasurement> actual = service.fetchMeasurementsFor(contextName, MATCHER_PREFIX, countdown);
        Collections.sort(actual);
        final List<RmiStageTimeMeasurement> expected = measurements.stream()
                .sorted()
                .map(RmiStageTimeMeasurement::new)
                .collect(Collectors.toList());
        Assertions.assertIterableEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("measurementProvider")
    void testGetSnapshotShouldCountAndGroupMeasurementWhenCalled(
            final List<StageTimeMeasurement> measurements) throws RemoteException {
        //given
        final MissionHealthCheckMatcher matcher = mock(MissionHealthCheckMatcher.class);
        final Registry registry = RmiServiceProvider.lookupRegistry(port);
        when(matcher.getName()).thenReturn(MATCHER_PREFIX);
        final RmiBackedStageStatisticsCollector underTestCountdown =
                new RmiBackedStageStatisticsCollector(contextName, matcher, registry, true);
        final RmiBackedStageStatisticsCollector underTestMission =
                new RmiBackedStageStatisticsCollector(contextName, matcher, registry, false);
        for (final StageTimeMeasurement m : measurements) {
            final boolean countdown = m.getTestCaseId().equals(CLASS_ONLY);
            service.insertStageTimeMeasurement(contextName, MATCHER_PREFIX, countdown, new RmiStageTimeMeasurement(m));
        }

        //when
        final StageStatisticsSnapshot actualCountdown = underTestCountdown.getSnapshot();
        final StageStatisticsSnapshot actualMission = underTestMission.getSnapshot();

        //then
        verify(matcher, atLeastOnce()).getName();
        assertSnapshotCountersMatch(measurements, actualCountdown, actualMission);
    }

    @ParameterizedTest
    @MethodSource("measurementProvider")
    void testFetchAllShouldReturnAllSavedMeasurementsInTheRightOrderWhenCalled(
            final List<StageTimeMeasurement> measurements) throws RemoteException {
        //given
        final boolean countdown = false;
        final Registry registry = RmiServiceProvider.lookupRegistry(port);
        final MissionHealthCheckMatcher matcher = mock(MissionHealthCheckMatcher.class);
        when(matcher.getName()).thenReturn(MATCHER_PREFIX);
        final RmiBackedStageStatisticsCollector underTest =
                new RmiBackedStageStatisticsCollector(contextName, matcher, registry, countdown);
        for (final StageTimeMeasurement m : measurements) {
            service.insertStageTimeMeasurement(contextName, MATCHER_PREFIX, countdown, new RmiStageTimeMeasurement(m));
        }

        //when
        final List<StageTimeMeasurement> actual = underTest.doFetchAll(contextName, matcher, countdown);

        //then
        verify(matcher, atLeastOnce()).getName();
        Assertions.assertIterableEquals(measurements.stream()
                .sorted()
                .collect(Collectors.toList()), actual);
    }
}
