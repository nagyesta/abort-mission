package com.github.nagyesta.abortmission.strongback.rmi;

import com.github.nagyesta.abortmission.core.telemetry.ReportingHelper;
import com.github.nagyesta.abortmission.core.telemetry.stats.LaunchTelemetryDataSource;
import com.github.nagyesta.abortmission.strongback.base.StrongbackController;
import com.github.nagyesta.abortmission.strongback.rmi.server.RmiServerManager;
import com.github.nagyesta.abortmission.strongback.rmi.server.RmiServiceProvider;
import com.github.nagyesta.abortmission.strongback.rmi.telemetry.RmiBackedLaunchTelemetryDataSource;

import java.rmi.registry.Registry;

/**
 * RMI specific implementation of {@link StrongbackController}.
 */
public class RmiStrongbackController implements StrongbackController {

    private final RmiServerManager serverManager;

    public RmiStrongbackController(final RmiServerManager serverManager) {
        this.serverManager = serverManager;
    }

    @Override
    public void erect() {
        serverManager.startServer();
    }

    @Override
    public void retract() {
        final Registry registry = RmiServiceProvider.lookupRegistry(serverManager.getPort());
        final LaunchTelemetryDataSource telemetryDataSource = new RmiBackedLaunchTelemetryDataSource(registry);
        new ReportingHelper().report(telemetryDataSource);
        serverManager.stopServer();
    }
}
