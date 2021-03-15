package com.github.nagyesta.abortmission.strongback.h2.telemetry;

import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import com.github.nagyesta.abortmission.core.telemetry.converter.BaseLaunchTelemetryConverter;
import com.github.nagyesta.abortmission.core.telemetry.converter.ClassTelemetryConverter;
import com.github.nagyesta.abortmission.core.telemetry.stats.ClassTelemetry;
import com.github.nagyesta.abortmission.core.telemetry.stats.LaunchTelemetryDataSource;
import com.github.nagyesta.abortmission.strongback.h2.repository.LaunchStatisticsRepository;
import com.github.nagyesta.abortmission.strongback.h2.server.H2DataSourceProvider;
import org.jdbi.v3.core.Jdbi;

import javax.sql.DataSource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement.CLASS_ONLY;

/**
 * H2 DB backed implementation of {@link LaunchTelemetryDataSource}.
 */
public class H2BackedLaunchTelemetryDataSource extends BaseLaunchTelemetryConverter implements LaunchTelemetryDataSource {

    private final Jdbi jdbi;

    /**
     * Creates the {@link LaunchTelemetryDataSource} using the provided JDBC data source.
     *
     * @param dataSource The JDBC data source.
     */
    public H2BackedLaunchTelemetryDataSource(final DataSource dataSource) {
        super(new ClassTelemetryConverter());
        this.jdbi = H2DataSourceProvider.jdbi(dataSource);
    }

    @Override
    public SortedMap<String, ClassTelemetry> fetchClassStatistics() {
        final Map<String, List<StageTimeMeasurement>> measurementsPerMatcher = jdbi.withExtension(LaunchStatisticsRepository.class,
                dao -> {
                    final List<String> matcherName = dao.fetchAllMatcherNames();
                    return matcherName.stream()
                            .collect(Collectors.toMap(Function.identity(), dao::fetchAllMeasurementsForMatcher));
                });
        return processFetchedRecords(measurementsPerMatcher);
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
