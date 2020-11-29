package com.github.nagyesta.abortmission.reporting.html;

public enum StageResultHtml {
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
    SUCCESS;

    public String lowerCaseName() {
        return this.name().toLowerCase();
    }
}
