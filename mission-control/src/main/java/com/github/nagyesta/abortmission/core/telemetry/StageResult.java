package com.github.nagyesta.abortmission.core.telemetry;

/**
 * Enum representing the outcome of a test preparation/run as observed by the
 * {@link com.github.nagyesta.abortmission.core.healthcheck.impl.StageStatisticsCollector}.
 */
public enum StageResult {
    /**
     * Failure observed.
     */
    FAILURE,
    /**
     * Execution aborted.
     */
    ABORT,
    /**
     * Reporting was suppressed.
     */
    SUPPRESSED,
    /**
     * Execution finished successfully.
     */
    SUCCESS
}
