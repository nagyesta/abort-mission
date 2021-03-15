package com.github.nagyesta.abortmission.strongback.h2.repository;

import com.github.nagyesta.abortmission.core.healthcheck.StageStatisticsSnapshot;
import com.github.nagyesta.abortmission.core.healthcheck.impl.DefaultStageStatisticsSnapshot;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.core.transaction.TransactionIsolationLevel;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * The repository we will use for data access.
 */
public interface LaunchStatisticsRepository {

    /**
     * Inserts a single time measurement to the database.
     *
     * @param contextName The name of the Abort-Mission context.
     * @param matcherName The name of the matcher we need to filter for.
     * @param countdown   True if we need the countdown values false otherwise.
     * @param measurement The measurement we observed.
     */
    @SqlUpdate("INSERT INTO LAUNCH_STATISTICS("
            + "  CONTEXT_NAME,"
            + "  MATCHER_NAME,"
            + "  LAUNCH_ID,"
            + "  COUNTDOWN,"
            + "  TEST_CLASS,"
            + "  TEST_METHOD,"
            + "  TEST_RESULT,"
            + "  START_MILLIS,"
            + "  END_MILLIS) "
            + "VALUES ("
            + "  :contextName,"
            + "  :matcherName,"
            + "  :launchId,"
            + "  :countdown,"
            + "  :testClassId,"
            + "  :testCaseId,"
            + "  :result,"
            + "  :start,"
            + "  :end)")
    @Transaction(value = TransactionIsolationLevel.READ_COMMITTED)
    void insertStageTimeMeasurement(@Bind("contextName") String contextName,
                                    @Bind("matcherName") String matcherName,
                                    @Bind("countdown") boolean countdown,
                                    @BindBean StageTimeMeasurement measurement);

    /**
     * Fetches all previously recorded measurements for the given matcher name.
     * The order of entries will be by start, end, class, method, launchId, result.
     *
     * @param matcherName The name of the matcher we need to filter for.
     * @return The matching measurements.
     */
    @SqlQuery("SELECT"
            + "  LAUNCH_ID as launchId,"
            + "  TEST_CLASS as testClassId,"
            + "  TEST_METHOD as testCaseId,"
            + "  TEST_RESULT as result,"
            + "  START_MILLIS as start,"
            + "  END_MILLIS as end "
            + "FROM LAUNCH_STATISTICS "
            + "WHERE MATCHER_NAME = :matcherName "
            + "ORDER BY START_MILLIS, END_MILLIS, TEST_CLASS, TEST_METHOD, LAUNCH_ID, TEST_RESULT")
    @RegisterConstructorMapper(StageTimeMeasurement.class)
    @Transaction(value = TransactionIsolationLevel.READ_COMMITTED, readOnly = true)
    List<StageTimeMeasurement> fetchAllMeasurementsForMatcher(@Bind("matcherName") String matcherName);

    /**
     * Fetches all previously recorded measurements for the given matcher name.
     * The order of entries will be by start, end, class, method, launchId, result.
     *
     * @param contextName The name of the Abort-Mission context.
     * @param matcherName The name of the matcher we need to filter for.
     * @param countdown   True if we need the countdown values false otherwise.
     * @return The matching measurements.
     */
    @SqlQuery("SELECT"
            + "  LAUNCH_ID as launchId,"
            + "  TEST_CLASS as testClassId,"
            + "  TEST_METHOD as testCaseId,"
            + "  TEST_RESULT as result,"
            + "  START_MILLIS as start,"
            + "  END_MILLIS as end "
            + "FROM LAUNCH_STATISTICS "
            + "WHERE CONTEXT_NAME = :contextName"
            + " AND MATCHER_NAME = :matcherName"
            + " AND COUNTDOWN = :countdown "
            + "ORDER BY START_MILLIS, END_MILLIS, TEST_CLASS, TEST_METHOD, LAUNCH_ID, TEST_RESULT")
    @RegisterConstructorMapper(StageTimeMeasurement.class)
    @Transaction(value = TransactionIsolationLevel.READ_COMMITTED, readOnly = true)
    List<StageTimeMeasurement> fetchMeasurementsFor(@Bind("contextName") String contextName,
                                                    @Bind("matcherName") String matcherName,
                                                    @Bind("countdown") boolean countdown);

    /**
     * Fetches all matcher names we used so far.
     *
     * @return The matcher names.
     */
    @SqlQuery("SELECT DISTINCT MATCHER_NAME "
            + "FROM LAUNCH_STATISTICS "
            + "ORDER BY MATCHER_NAME")
    @Transaction(value = TransactionIsolationLevel.READ_COMMITTED, readOnly = true)
    List<String> fetchAllMatcherNames();

    /**
     * Gets a snapshot of the previously recorded measurements for abort evaluation.
     *
     * @param contextName The name of the Abort-Mission context.
     * @param matcherName The name of the matcher we need to filter for.
     * @param countdown   True if we need the countdown values false otherwise.
     * @return The snapshot.
     */
    @SqlQuery("SELECT"
            + "  SUM(CASE WHEN TEST_RESULT = 0 THEN 1 ELSE 0 END) as failed,"
            + "  SUM(CASE WHEN TEST_RESULT = 1 THEN 1 ELSE 0 END) as aborted,"
            + "  SUM(CASE WHEN TEST_RESULT = 2 THEN 1 ELSE 0 END) as suppressed,"
            + "  SUM(CASE WHEN TEST_RESULT = 3 THEN 1 ELSE 0 END) as succeeded "
            + "FROM LAUNCH_STATISTICS "
            + "WHERE CONTEXT_NAME = :contextName"
            + " AND MATCHER_NAME = :matcherName"
            + " AND COUNTDOWN = :countdown")
    @RegisterRowMapper(H2BackedStageStatisticsSnapshotMapper.class)
    @Transaction(value = TransactionIsolationLevel.READ_COMMITTED, readOnly = true)
    StageStatisticsSnapshot getSnapshot(@Bind("contextName") String contextName,
                                        @Bind("matcherName") String matcherName,
                                        @Bind("countdown") boolean countdown);

    /**
     * Mapper class for calling the {@link StageStatisticsSnapshot} constructor properly.
     */
    class H2BackedStageStatisticsSnapshotMapper implements RowMapper<StageStatisticsSnapshot> {

        @Override
        public StageStatisticsSnapshot map(final ResultSet rs, final StatementContext ctx) throws SQLException {
            return new DefaultStageStatisticsSnapshot(
                    rs.getInt("failed"),
                    rs.getInt("succeeded"),
                    rs.getInt("aborted"),
                    rs.getInt("suppressed"));
        }
    }
}
