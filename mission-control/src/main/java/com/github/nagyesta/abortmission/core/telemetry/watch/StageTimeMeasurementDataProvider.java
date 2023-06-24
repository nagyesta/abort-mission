package com.github.nagyesta.abortmission.core.telemetry.watch;

import java.util.List;
import java.util.UUID;

/**
 * Defines the contract for the classes that can be used as data providers when creating stage time measurement instances.
 */
public interface StageTimeMeasurementDataProvider {
    /**
     * Returns the unique ID of the test method execution (must be unique even in case the same test is re-run).
     *
     * @return the unique ID
     */
    UUID getLaunchId();

    /**
     * Returns the unique ID of the test class.
     *
     * @return the unique ID
     */
    String getTestClassId();

    /**
     * Returns the unique ID of the test case (within the test class).
     *
     * @return the unique ID
     */
    String getTestCaseId();

    /**
     * Returns the name that should be displayed in the reports.
     *
     * @return the display name
     */
    String getDisplayName();

    /**
     * Returns the time when the stage execution started.
     *
     * @return the start time
     */
    long getStart();

    /**
     * Returns the time when the stage execution ended. Can be null while the execution is still running.
     *
     * @return the end time
     */
    Long getEnd();

    /**
     * Returns the name of the thread that executed the stage.
     *
     * @return the thread name
     */
    String getThreadName();

    /**
     * Returns the class name of the exception that was thrown during the stage execution.
     *
     * @return the class name
     */
    String getThrowableClass();

    /**
     * Returns the message of the exception that was thrown during the stage execution.
     *
     * @return the message
     */
    String getThrowableMessage();

    /**
     * Returns the stack trace of the exception that was thrown during the stage execution.
     *
     * @return the stack trace
     */
    List<String> getStackTrace();
}
