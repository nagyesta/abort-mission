package com.github.nagyesta.abortmission.strongback.rmi.server;

import com.github.nagyesta.abortmission.strongback.base.StrongbackException;
import com.github.nagyesta.abortmission.strongback.rmi.service.LaunchStatisticsService;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Utility class for data source creation.
 */
public final class RmiServiceProvider {

    private RmiServiceProvider() {
        //util class
    }

    /**
     * Creates the default Registry using the provided port.
     *
     * @param portNumber The port we need to use.
     * @return registry
     */
    public static Registry createRegistry(final int portNumber) {
        try {
            return LocateRegistry.createRegistry(portNumber);
        } catch (final RemoteException e) {
            throw new StrongbackException("Failed to create registry for port: " + portNumber, e);
        }
    }

    /**
     * Looks up the default Registry using the provided port.
     *
     * @param portNumber The port we need to use.
     * @return registry
     */
    public static Registry lookupRegistry(final int portNumber) {
        try {
            return LocateRegistry.getRegistry(portNumber);
        } catch (final RemoteException e) {
            throw new StrongbackException("Failed to locate registry for port: " + portNumber, e);
        }
    }

    /**
     * Looks up a service stub from a registry.
     *
     * @param registry The registry we are connecting to.
     * @return service
     */
    public static LaunchStatisticsService service(final Registry registry) {
        try {
            return (LaunchStatisticsService) registry.lookup(RmiServerConstants.SERVICE_NAME);
        } catch (final Exception e) {
            throw new StrongbackException("Failed to get service instance.", e);
        }
    }

}
