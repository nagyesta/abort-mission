package com.github.nagyesta.abortmission.strongback.h2.server;

import com.github.nagyesta.abortmission.strongback.base.StrongbackConfigurationHelper;
import com.github.nagyesta.abortmission.strongback.base.StrongbackException;
import org.h2.tools.Server;

import java.sql.Connection;
import java.sql.SQLException;

import static com.github.nagyesta.abortmission.strongback.h2.server.H2ServerConstants.*;

/**
 * Manages the lifecycle of the H2 server we will use.
 */
public class H2ServerManager {

    private final String password;
    private final int port;
    private final boolean startStopNeeded;

    /**
     * Creates the instance using the values provided as system properties.
     */
    public H2ServerManager() {
        this(StrongbackConfigurationHelper.optionalServerPassword().orElse(DEFAULT_H2_TCP_PASSWORD),
                StrongbackConfigurationHelper.optionalPortValue().orElse(DEFAULT_H2_PORT),
                !StrongbackConfigurationHelper.useExternalServer());
    }

    /**
     * Creates the instance using the explicitly provided values.
     *
     * @param password        The TCP password used for the server connection.
     * @param port            The TCP port we need to use.
     * @param startStopNeeded The feature flag telling us whether we need to start/stop the DB at all.
     */
    public H2ServerManager(final String password, final int port, final boolean startStopNeeded) {
        this.password = password;
        this.port = port;
        this.startStopNeeded = startStopNeeded;
    }

    /**
     * Starts the server if needed.
     */
    public void startServer() {
        if (startStopNeeded) {
            doStart();
        }
    }

    /**
     * Stops the server if needed.
     */
    public void stopServer() {
        if (startStopNeeded) {
            doStop();
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

    private void doStart() {
        try {
            Server.createTcpServer("-ifNotExists", "-tcpPassword", password, "-tcpPort", String.valueOf(port))
                    .start();
        } catch (final Exception ex) {
            throw new StrongbackException("Server startup failed.", ex);
        }
    }

    private void doStop() {
        try {
            sendShutdownCommand();
            Server.shutdownTcpServer(String.format(DEFAULT_TCP_URL_FORMAT, port), password, true, false);
        } catch (final Exception ex) {
            throw new StrongbackException("Server failed to stop.", ex);
        }
    }

    @SuppressWarnings("LocalCanBeFinal")
    private void sendShutdownCommand() throws SQLException {
        try (Connection connection = H2DataSourceProvider.createDefaultDataSource(port).getConnection()) {
            connection.createStatement().executeUpdate("SHUTDOWN");
        }
    }
}
