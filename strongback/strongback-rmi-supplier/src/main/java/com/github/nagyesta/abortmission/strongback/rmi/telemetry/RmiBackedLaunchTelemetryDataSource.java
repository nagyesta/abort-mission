package com.github.nagyesta.abortmission.strongback.rmi.telemetry;

import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import com.github.nagyesta.abortmission.core.telemetry.converter.BaseLaunchTelemetryConverter;
import com.github.nagyesta.abortmission.core.telemetry.converter.ClassTelemetryConverter;
import com.github.nagyesta.abortmission.core.telemetry.stats.ClassTelemetry;
import com.github.nagyesta.abortmission.core.telemetry.stats.LaunchTelemetryDataSource;
import com.github.nagyesta.abortmission.strongback.base.StrongbackException;
import com.github.nagyesta.abortmission.strongback.rmi.server.RmiServiceProvider;
import com.github.nagyesta.abortmission.strongback.rmi.service.LaunchStatisticsService;
import com.github.nagyesta.abortmission.strongback.rmi.stats.RmiStageTimeMeasurement;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement.CLASS_ONLY;

/**
 * Rmi backed implementation of {@link LaunchTelemetryDataSource}.
 */
public class RmiBackedLaunchTelemetryDataSource extends BaseLaunchTelemetryConverter implements LaunchTelemetryDataSource {

    private final Registry registry;

    /**
     * Creates the {@link LaunchTelemetryDataSource} using the provided RMI registry.
     *
     * @param registry The RMI registry.
     */
    public RmiBackedLaunchTelemetryDataSource(final Registry registry) {
        super(new ClassTelemetryConverter());
        this.registry = registry;
    }

    @Override
    public SortedMap<String, ClassTelemetry> fetchClassStatistics() {
        try {
            return doFetchStatistics();
        } catch (final RemoteException e) {
            throw new StrongbackException(e.getMessage(), e);
        }
    }

    private SortedMap<String, ClassTelemetry> doFetchStatistics() throws RemoteException {
        final LaunchStatisticsService service = RmiServiceProvider.service(registry);
        final List<String> matcherName = service.fetchAllMatcherNames();
        final Map<String, List<StageTimeMeasurement>> measurementsPerMatcher = matcherName.stream()
                .collect(Collectors.toMap(Function.identity(),
                        name -> fetchByMatcherName(service, name)));
        return processFetchedRecords(measurementsPerMatcher);
    }

    private List<StageTimeMeasurement> fetchByMatcherName(final LaunchStatisticsService service, final String name) {
        try {
            return service.fetchAllMeasurementsForMatcher(name).stream()
                    .map(RmiStageTimeMeasurement::toStageTimeMeasurement)
                    .collect(Collectors.toList());
        } catch (final RemoteException e) {
            throw new StrongbackException(e.getMessage(), e);
        }
    }

    private SortedMap<String, ClassTelemetry> processFetchedRecords(
            final Map<String, List<StageTimeMeasurement>> measurementsPerMatcher) {
        final Map<String, Map<String, Set<String>>> matchersByClassAndMethod = new TreeMap<>();
        final Map<String, List<StageTimeMeasurement>> byTestClassAccumulator = new TreeMap<>();
        measurementsPerMatcher.forEach((matcher, measurements) -> {
            mergeInto(matchersByClassAndMethod,
                    measurements.stream().filter(s -> CLASS_ONLY.equals(s.getTestCaseId())),
                    measurements.stream().filter(s -> !CLASS_ONLY.equals(s.getTestCaseId())),
                    matcher);
            mergeInto(byTestClassAccumulator,
                    measurements.stream().filter(s -> CLASS_ONLY.equals(s.getTestCaseId())),
                    measurements.stream().filter(s -> !CLASS_ONLY.equals(s.getTestCaseId())));
        });
        return repartitionByClasses(matchersByClassAndMethod, byTestClassAccumulator);
    }

}
