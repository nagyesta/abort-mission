package com.github.nagyesta.abortmission.strongback.rmi.service.impl;

import com.github.nagyesta.abortmission.strongback.rmi.server.RmiServerConstants;
import com.github.nagyesta.abortmission.strongback.rmi.service.RmiExitService;

import java.rmi.Remote;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Class taking care of the registered remotes and shutting down the service.
 */
public class RmiExitServiceImpl implements RmiExitService {

    private final Remote remote;
    private final Thread thread;
    private final Registry registry;

    /**
     * Constructor registering the service we need to stop.
     *
     * @param remote   The service.
     * @param thread   The thread started when the registry was created (and waiting since).
     * @param registry The registry we created.
     */
    public RmiExitServiceImpl(final Remote remote, final Thread thread, final Registry registry) {
        this.remote = remote;
        this.thread = thread;
        this.registry = registry;
    }

    @Override
    public void shutdown() {
        try {
            registry.unbind(RmiServerConstants.SERVICE_NAME);
            registry.unbind(RmiServerConstants.EXIT_SERVICE_NAME);

            UnicastRemoteObject.unexportObject(remote, true);
            UnicastRemoteObject.unexportObject(this, true);

            System.out.println("CalculatorServer exiting.");
            thread.interrupt();
        } catch (final Exception e) {
            //ignore
        }
    }
}
