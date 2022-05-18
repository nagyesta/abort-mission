package com.github.nagyesta.abortmission.strongback.rmi.server;

/**
 * Defines the RMI database specific default values and constants.
 */
public final class RmiServerConstants {
    /**
     * Default RMI port.
     */
    public static final int DEFAULT_RMI_PORT = 29542;
    /**
     * The RMI service name used for lookup of the abort mission service.
     */
    public static final String SERVICE_NAME = "LaunchStatisticsService";
    /**
     * The RMI service name used for lookup of the service that can shut down the server.
     */
    public static final String EXIT_SERVICE_NAME = "ExitService";

    private RmiServerConstants() {
        //util class
    }
}
