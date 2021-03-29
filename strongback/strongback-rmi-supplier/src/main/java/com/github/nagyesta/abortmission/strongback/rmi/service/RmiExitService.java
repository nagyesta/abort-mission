package com.github.nagyesta.abortmission.strongback.rmi.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Defines how we need to unregister remotes and shutting down the service.
 */
public interface RmiExitService extends Remote {

    /**
     * unregisters remotes and shuts down.
     *
     * @throws RemoteException When the RMI call fails.
     */
    void shutdown() throws RemoteException;
}
