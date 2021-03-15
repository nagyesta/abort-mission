package com.github.nagyesta.abortmission.strongback.h2.server;

/**
 * Defines the H2 database specific default values and constants.
 */
public final class H2ServerConstants {
    /**
     * Default H2 username for JDBC.
     */
    public static final String DEFAULT_H2_JDBC_USER_NAME = "sa";
    /**
     * Default H2 password for JDBC.
     */
    public static final String DEFAULT_H2_JDBC_PASSWORD = "";
    /**
     * Default H2 TCP password.
     */
    public static final String DEFAULT_H2_TCP_PASSWORD = "4b0RtM1$$ioN";
    /**
     * Default H2 TCP port.
     */
    public static final int DEFAULT_H2_PORT = 29542;
    /**
     * Default H2 host name used in the URL.
     */
    public static final String DEFAULT_H2_HOST = "localhost";
    /**
     * The String format used for the TCP server creation.
     */
    public static final String DEFAULT_TCP_URL_FORMAT = "tcp://" + DEFAULT_H2_HOST + ":%d";
    /**
     * The String format used for the creation of the in-memory DB.
     */
    public static final String IN_MEMORY_DB_FORMAT = "/mem:abortMission-%d;DB_CLOSE_DELAY=-1";
    /**
     * The String format used for JDBC connections.
     */
    public static final String DEFAULT_H2_JDBC_CONNECTION_URL_FORMAT = "jdbc:h2:" + DEFAULT_TCP_URL_FORMAT + IN_MEMORY_DB_FORMAT;

    private H2ServerConstants() {
        //util class
    }
}
