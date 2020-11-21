package com.github.nagyesta.abortmission.core.telemetry.stats;

import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static com.github.nagyesta.abortmission.core.telemetry.StageResult.*;
import static com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement.CLASS_ONLY;

@SuppressWarnings("checkstyle:JavadocVariable")
public abstract class AbstractTelemetryTest {

    protected static final UUID FIRST = UUID.randomUUID();
    protected static final UUID SECOND = UUID.randomUUID();
    protected static final UUID THIRD = UUID.randomUUID();
    protected static final String CLASS = "class";
    protected static final String METHOD = "method";
    protected static final int MS_1 = 1;
    protected static final int MS_2 = 2;
    protected static final int MS_5 = 5;
    protected static final int MS_10 = 10;
    protected static final int MS_20 = 20;
    protected static final StageTimeMeasurement FIRST_PASS_1_10 = new StageTimeMeasurement(FIRST, CLASS, CLASS_ONLY, SUCCESS, MS_1, MS_10);
    protected static final StageTimeMeasurement FIRST_FAIL_1_10 = new StageTimeMeasurement(FIRST, CLASS, CLASS_ONLY, FAILURE, MS_1, MS_10);
    protected static final StageTimeMeasurement SECOND_PASS_2_20 = new StageTimeMeasurement(SECOND, CLASS, METHOD, SUCCESS, MS_2, MS_20);
    protected static final StageTimeMeasurement THIRD_PASS_5_5 = new StageTimeMeasurement(THIRD, CLASS, METHOD, SUCCESS, MS_5, MS_5);
    protected static final StageTimeMeasurement THIRD_ABORT_5_5 = new StageTimeMeasurement(THIRD, CLASS, METHOD, ABORT, MS_5, MS_5);
    protected static final StageTimeMeasurement THIRD_SUPPRESS_5_5 = new StageTimeMeasurement(THIRD, CLASS, METHOD, SUPPRESSED, MS_5, MS_5);
    protected static final LocalDateTime LOCAL_DATE_TIME_1 = LocalDateTime.ofInstant(Instant.ofEpochMilli(MS_1), ZoneId.systemDefault());
    protected static final LocalDateTime LOCAL_DATE_TIME_10 = LocalDateTime.ofInstant(Instant.ofEpochMilli(MS_10), ZoneId.systemDefault());
    protected static final LocalDateTime LOCAL_DATE_TIME_20 = LocalDateTime.ofInstant(Instant.ofEpochMilli(20), ZoneId.systemDefault());
    protected static final LocalDateTime LOCAL_DATE_TIME_2 = LocalDateTime.ofInstant(Instant.ofEpochMilli(2), ZoneId.systemDefault());

}
