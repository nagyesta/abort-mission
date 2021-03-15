package com.github.nagyesta.abortmission.strongback.rmi.service;

import com.github.nagyesta.abortmission.strongback.rmi.stats.RmiStageStatisticsSnapshot;
import com.github.nagyesta.abortmission.strongback.rmi.stats.RmiStageTimeMeasurement;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * The repository we will use for data access.
 */
public interface LaunchStatisticsService extends Remote {

    /**
     * Inserts a single time measurement to the database.
     *
     * @param contextName The name of the Abort-Mission context.
     * @param matcherName The name of the matcher we need to filter for.
     * @param countdown   True if we need the countdown values false otherwise.
     * @param measurement The measurement we captured during stage execution.
     * @throws RemoteException When the RMI call fails.
     */
    void insertStageTimeMeasurement(String contextName,
                                    String matcherName,
                                    boolean countdown,
                                    RmiStageTimeMeasurement measurement) throws RemoteException;

    /**
     * Fetches all previously recorded measurements for the given matcher name.
     * The order of entries will be by start, end, class, method, launchId, result.
     *
     * @param matcherName The name of the matcher we need to filter for.
     * @return The matching measurements.
     * @throws RemoteException When the RMI call fails.
     */
    List<RmiStageTimeMeasurement> fetchAllMeasurementsForMatcher(String matcherName) throws RemoteException;

    /**
     * Fetches all previously recorded measurements for the given matcher name.
     * The order of entries will be by start, end, class, method, launchId, result.
     *
     * @param contextName The name of the Abort-Mission context.
     * @param matcherName The name of the matcher we need to filter for.
     * @param countdown   True if we need the countdown values false otherwise.
     * @return The matching measurements.
     * @throws RemoteException When the RMI call fails.
     */
    List<RmiStageTimeMeasurement> fetchMeasurementsFor(String contextName,
                                                       String matcherName,
                                                       boolean countdown) throws RemoteException;

    /**
     * Fetches all matcher names we used so far.
     *
     * @return The matcher names.
     * @throws RemoteException When the RMI call fails.
     */
    List<String> fetchAllMatcherNames() throws RemoteException;

    /**
     * Gets a snapshot of the previously recorded measurements for abort evaluation.
     *
     * @param contextName The name of the Abort-Mission context.
     * @param matcherName The name of the matcher we need to filter for.
     * @param countdown   True if we need the countdown values false otherwise.
     * @return The snapshot.
     * @throws RemoteException When the RMI call fails.
     */
    RmiStageStatisticsSnapshot getSnapshot(String contextName,
                                           String matcherName,
                                           boolean countdown) throws RemoteException;
}
