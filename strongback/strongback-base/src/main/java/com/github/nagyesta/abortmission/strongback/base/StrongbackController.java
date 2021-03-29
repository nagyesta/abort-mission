package com.github.nagyesta.abortmission.strongback.base;

/**
 * Controls lifecycle steps related to the strongback.
 */
public interface StrongbackController {

    /**
     * Prepares for the launch by setting up the supporting system we will use for the tests.
     */
    void erect();

    /**
     * Exports telemetry data and tears down the supporting system after the tests.
     */
    void retract();
}
