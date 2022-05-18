package com.github.nagyesta.abortmission.strongback.rmi.service;

import com.github.nagyesta.abortmission.core.healthcheck.StageStatisticsSnapshot;
import com.github.nagyesta.abortmission.core.telemetry.StageResult;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import com.github.nagyesta.abortmission.strongback.base.StrongbackException;
import com.github.nagyesta.abortmission.strongback.rmi.server.RmiServerConstants;
import com.github.nagyesta.abortmission.strongback.rmi.server.RmiServiceProvider;
import com.github.nagyesta.abortmission.strongback.rmi.service.impl.SingletonLaunchStatisticsService;
import com.github.nagyesta.abortmission.strongback.rmi.stats.RmiStageStatisticsSnapshot;
import com.github.nagyesta.abortmission.strongback.rmi.stats.RmiStageTimeMeasurement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement.CLASS_ONLY;

@Tag("integration")
@SuppressWarnings({"checkstyle:JavadocVariable", "checkstyle:VisibilityModifier", "checkstyle:DesignForExtension"})
public class AbstractInMemoryDataSourceIntegrationTest {

    protected static final String MATCHER_PREFIX = "matcher-";
    protected static final String METHOD_PREFIX = "methodName";
    protected static final String CLASS_NAME = "com.github.nagyesta.abortmission."
            + "strongback.h2.repository.AbstractInMemoryDataSourceIntegrationTest";

    protected final String contextName;
    protected int port;
    protected LaunchStatisticsService service;
    protected Registry registry;

    protected AbstractInMemoryDataSourceIntegrationTest(final String contextName, final int port) {
        this.contextName = contextName;
        this.port = port;
        this.registry = RmiServiceProvider.createRegistry(port);
    }

    protected static Map<String, List<StageTimeMeasurement>> mapFromStream(final Stream<Integer> stream) {
        return stream.collect(Collectors.toMap(
                i -> MATCHER_PREFIX + i,
                i -> Collections.singletonList(generateMeasurement(i))));
    }

    protected static Map<String, List<StageTimeMeasurement>> mapFromMultiStream(final Stream<Integer> stream) {
        return stream.collect(Collectors.toMap(
                i -> MATCHER_PREFIX + i,
                i -> IntStream.rangeClosed(0, i).boxed()
                        .map(AbstractInMemoryDataSourceIntegrationTest::generateMeasurement)
                        .collect(Collectors.toList())));
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    protected static StageTimeMeasurement generateMeasurement(final Integer i) {
        final String testCaseId = Optional.of(i % 3)
                .filter(n -> n != 0)
                .map(n -> METHOD_PREFIX + n)
                .orElse(CLASS_ONLY);
        return new StageTimeMeasurement(UUID.randomUUID(),
                CLASS_NAME,
                testCaseId,
                StageResult.values()[i % StageResult.values().length],
                i, i + i);
    }

    protected void assertSnapshotCountersMatch(final List<StageTimeMeasurement> expected,
                                               final RmiStageStatisticsSnapshot actualCountdown,
                                               final RmiStageStatisticsSnapshot actualMission) {
        final Map<StageResult, Integer> expectedCountdown = filterAndCount(expected, s -> s.getTestCaseId().equals(CLASS_ONLY));
        assertResultsMatch(expectedCountdown, actualCountdown);

        final Map<StageResult, Integer> expectedMission = filterAndCount(expected, s -> !s.getTestCaseId().equals(CLASS_ONLY));
        assertResultsMatch(expectedMission, actualMission);
    }


    protected void assertSnapshotCountersMatch(final List<StageTimeMeasurement> expected,
                                               final StageStatisticsSnapshot actualCountdown,
                                               final StageStatisticsSnapshot actualMission) {
        final Map<StageResult, Integer> expectedCountdown = filterAndCount(expected, s -> s.getTestCaseId().equals(CLASS_ONLY));
        assertResultsMatch(expectedCountdown, actualCountdown);

        final Map<StageResult, Integer> expectedMission = filterAndCount(expected, s -> !s.getTestCaseId().equals(CLASS_ONLY));
        assertResultsMatch(expectedMission, actualMission);
    }

    private Map<StageResult, Integer> filterAndCount(final List<StageTimeMeasurement> expected,
                                                     final Predicate<StageTimeMeasurement> predicate) {
        return expected
                .stream()
                .filter(predicate)
                .collect(Collectors.groupingBy(StageTimeMeasurement::getResult))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size()));
    }

    private void assertResultsMatch(final Map<StageResult, Integer> expected, final RmiStageStatisticsSnapshot actual) {
        Assertions.assertEquals(expected.getOrDefault(StageResult.FAILURE, 0), actual.getFailed());
        Assertions.assertEquals(expected.getOrDefault(StageResult.ABORT, 0), actual.getAborted());
        Assertions.assertEquals(expected.getOrDefault(StageResult.SUPPRESSED, 0), actual.getSuppressed());
        Assertions.assertEquals(expected.getOrDefault(StageResult.SUCCESS, 0), actual.getSucceeded());
    }

    private void assertResultsMatch(final Map<StageResult, Integer> expected, final StageStatisticsSnapshot actual) {
        Assertions.assertEquals(expected.getOrDefault(StageResult.FAILURE, 0), actual.getFailed());
        Assertions.assertEquals(expected.getOrDefault(StageResult.ABORT, 0), actual.getAborted());
        Assertions.assertEquals(expected.getOrDefault(StageResult.SUPPRESSED, 0), actual.getSuppressed());
        Assertions.assertEquals(expected.getOrDefault(StageResult.SUCCESS, 0), actual.getSucceeded());
    }

    protected void insertAll(final Map<String, List<StageTimeMeasurement>> input) throws RemoteException {
        for (final Map.Entry<String, List<StageTimeMeasurement>> entry : input.entrySet()) {
            final String matcherName = entry.getKey();
            final List<StageTimeMeasurement> measurementList = entry.getValue();
            for (final StageTimeMeasurement measurement : measurementList) {
                final boolean isCountdown = CLASS_ONLY.equals(measurement.getTestCaseId());
                service.insertStageTimeMeasurement(contextName, matcherName, isCountdown, new RmiStageTimeMeasurement(measurement));
            }
        }
    }

    @BeforeEach
    void setUp() {
        try {
            service = new SingletonLaunchStatisticsService();
            final Remote abortMissionService = UnicastRemoteObject.exportObject(service, port);
            if (Arrays.asList(registry.list()).contains(RmiServerConstants.SERVICE_NAME)) {
                registry.rebind(RmiServerConstants.SERVICE_NAME, abortMissionService);
            } else {
                registry.bind(RmiServerConstants.SERVICE_NAME, abortMissionService);
            }
        } catch (final Exception ex) {
            throw new StrongbackException("Server startup failed.", ex);
        }
    }
}
