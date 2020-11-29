package com.github.nagyesta.abortmission.reporting.json;

public enum StageResultJson {
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
