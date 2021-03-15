package com.github.nagyesta.abortmission.strongback.rmi.server;

import com.github.nagyesta.abortmission.strongback.base.StrongbackConfigurationHelper;
import com.github.nagyesta.abortmission.strongback.base.StrongbackException;
import com.github.nagyesta.abortmission.strongback.rmi.service.LaunchStatisticsService;
import com.github.nagyesta.abortmission.strongback.rmi.service.RmiExitService;
import com.github.nagyesta.abortmission.strongback.rmi.service.impl.LaunchStatisticsServiceImpl;
import com.github.nagyesta.abortmission.strongback.rmi.service.impl.RmiExitServiceImpl;

import java.rmi.Remote;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Manages the lifecycle of the RMI server we will use.
 */
@SuppressWarnings("FieldCanBeLocal")
public class RmiServerManager {

    private final int port;
    //keep a strong reference to the service to avoid unintended GC
    private LaunchStatisticsService launchStatisticsService;
    //keep a strong reference to the service to avoid unintended GC
    private RmiExitService rmiExitService;

    /**
     * Creates the instance using the values provided as system properties.
     */
    public RmiServerManager() {
        this(StrongbackConfigurationHelper.optionalPortValue().orElse(RmiServerConstants.DEFAULT_RMI_PORT));
    }

    /**
     * Creates the instance using the explicitly provided values.
     *
     * @param port The TCP port we need to use.
     */
    public RmiServerManager(final int port) {
        this.port = port;
    }

    /**
     * Starts the server if needed.
     */
    public void startServer() {
        try {
            doStart();
            Thread.currentThread().join();
        } catch (final Exception ex) {
            throw new StrongbackException("Server startup failed.", ex);
        }
    }

    /**
     * Stops the server if needed.
     */
    public void stopServer() {
        try {
            doStop();
        } catch (final Exception ex) {
            throw new StrongbackException("Server failed to stop.", ex);
        }
    }

    /**
     * Stops the server and ignores all exceptions.
     */
    public void stopServerQuietly() {
        try {
            stopServer();
        } catch (final Exception ignored) {
            //ignore
        }
    }

    /**
     * Returns the port number used by this server.
     *
     * @return The port.
     */
    public int getPort() {
        return port;
    }

    /**
     * Performs the startup.
     *
     * @throws Exception If the server couldn't be started or configured.
     */
    protected void doStart() throws Exception {
        final Registry registry = RmiServiceProvider.createRegistry(port);

        launchStatisticsService = new LaunchStatisticsServiceImpl();
        rmiExitService = new RmiExitServiceImpl(launchStatisticsService, Thread.currentThread(), registry);

        final Remote abortMissionService = UnicastRemoteObject.exportObject(launchStatisticsService, port);
        final Remote exitService = UnicastRemoteObject.exportObject(rmiExitService, port);

        registry.bind(RmiServerConstants.SERVICE_NAME, abortMissionService);
        registry.bind(RmiServerConstants.EXIT_SERVICE_NAME, exitService);
    }

    /**
     * Performs the shutdown steps.
     *
     * @throws Exception When the remote service call fails.
     */
    protected void doStop() throws Exception {
        final Registry registry = RmiServiceProvider.lookupRegistry(port);
        ((RmiExitService) registry.lookup(RmiServerConstants.EXIT_SERVICE_NAME)).shutdown();
    }
}
