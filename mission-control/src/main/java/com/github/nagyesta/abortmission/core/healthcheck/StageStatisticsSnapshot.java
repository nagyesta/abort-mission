package com.github.nagyesta.abortmission.core.healthcheck;

/**
 * Represents a read-only snapshot of the stage statistics we are capturing.
 */
public interface StageStatisticsSnapshot {

    /**
     * The total number of successful/failed/aborted executions.
     *
     * @return success + fail + abort
     */
    int getTotal();

    /**
     * The number of failed or aborted executions.
     *
     * @return fail + abort
     */
    int getNotSuccessful();

    /**
     * The number of failed executions.
     *
     * @return fail
     */
    int getFailed();

    /**
     * The number of aborted executions.
     *
     * @return abort
     */
    int getAborted();

    /**
     * The number of successful executions.
     *
     * @return success
     */
    int getSucceeded();

    /**
     * The number of suppressed (failure/abort logging) cases.
     *
     * @return suppressed
     */
    int getSuppressed();

}
