package com.github.nagyesta.abortmission.strongback.h2.repository.mapper;

import com.github.nagyesta.abortmission.core.telemetry.StageResult;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurementBuilder;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Maps the {@link StageTimeMeasurement} from the database column.
 */
public class StageTimeMeasurementMapper implements RowMapper<StageTimeMeasurement> {

    @Override
    public StageTimeMeasurement map(final ResultSet rs, final StatementContext ctx) throws SQLException {
        return StageTimeMeasurementBuilder.builder()
                .setLaunchId(UUID.fromString(rs.getString("launchId")))
                .setTestClassId(rs.getString("testClassId"))
                .setTestCaseId(rs.getString("testCaseId"))
                .setResult(StageResult.values()[rs.getInt("result")])
                .setStart(rs.getLong("start"))
                .setEnd(rs.getLong("end"))
                .setDisplayName(rs.getString("displayName"))
                .setThreadName(rs.getString("threadName"))
                .setThrowableClass(rs.getString("throwableClass"))
                .setThrowableMessage(rs.getString("throwableMessage"))
                .setStackTrace(Optional.ofNullable(rs.getString("stackTraceAsString"))
                        .map(s -> List.of(s.split(Pattern.quote(StageTimeMeasurement.STACKTRACE_LINE_SEPARATOR))))
                        .orElse(null))
                .build();
    }
}
