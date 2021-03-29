package com.github.nagyesta.abortmission.strongback.h2.server;

import com.github.nagyesta.abortmission.core.healthcheck.StageStatisticsSnapshot;
import com.github.nagyesta.abortmission.core.telemetry.StageResult;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import com.github.nagyesta.abortmission.strongback.h2.repository.LaunchStatisticsRepository;
import org.h2.jdbcx.JdbcDataSource;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.extension.ExtensionCallback;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

import javax.sql.DataSource;
import java.sql.SQLException;
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
    private final String url;
    protected Jdbi jdbi;
    protected DataSource dataSource;

    protected AbstractInMemoryDataSourceIntegrationTest(final String contextName, final String url) {
        this.contextName = contextName;
        this.url = url + ";DB_CLOSE_DELAY=-1";
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

    private void assertResultsMatch(final Map<StageResult, Integer> expected, final StageStatisticsSnapshot actual) {
        Assertions.assertEquals(expected.getOrDefault(StageResult.FAILURE, 0), actual.getFailed());
        Assertions.assertEquals(expected.getOrDefault(StageResult.ABORT, 0), actual.getAborted());
        Assertions.assertEquals(expected.getOrDefault(StageResult.SUPPRESSED, 0), actual.getSuppressed());
        Assertions.assertEquals(expected.getOrDefault(StageResult.SUCCESS, 0), actual.getSucceeded());
    }

    protected ExtensionCallback<Void, LaunchStatisticsRepository, RuntimeException> insertAllCallback(
            final Map<String, List<StageTimeMeasurement>> input) {
        return dao -> {
            input.forEach((matcherName, measurementList) -> {
                measurementList.forEach(measurement -> {
                    final boolean isCountdown = CLASS_ONLY.equals(measurement.getTestCaseId());
                    dao.insertStageTimeMeasurement(contextName, matcherName, isCountdown, measurement);
                });
            });
            return null;
        };
    }

    @BeforeEach
    void setUp() throws Exception {
        final JdbcDataSource jdbcDataSource = new JdbcDataSource();
        jdbcDataSource.setURL(url);
        this.dataSource = jdbcDataSource;
        jdbi = H2DataSourceProvider.jdbi(dataSource);
        jdbi.open().createUpdate("DROP ALL OBJECTS").execute();
        new H2SchemaInitializer(dataSource).initialize();
    }

    @AfterEach
    void tearDown() throws SQLException {
        jdbi.open().createUpdate("DROP ALL OBJECTS").execute();
    }
}
