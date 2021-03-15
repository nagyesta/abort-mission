package com.github.nagyesta.abortmission.strongback.h2;

import com.github.nagyesta.abortmission.core.telemetry.ReportingHelper;
import com.github.nagyesta.abortmission.core.telemetry.stats.LaunchTelemetryDataSource;
import com.github.nagyesta.abortmission.strongback.base.StrongbackController;
import com.github.nagyesta.abortmission.strongback.h2.server.H2SchemaInitializer;
import com.github.nagyesta.abortmission.strongback.h2.server.H2ServerManager;
import com.github.nagyesta.abortmission.strongback.h2.telemetry.H2BackedLaunchTelemetryDataSource;

import javax.sql.DataSource;

/**
 * H2 specific implementation of {@link StrongbackController}.
 */
public class H2StrongbackController implements StrongbackController {

    private final H2ServerManager serverManager;
    private final DataSource dataSource;

    public H2StrongbackController(final H2ServerManager serverManager, final DataSource dataSource) {
        this.serverManager = serverManager;
        this.dataSource = dataSource;
    }

    @Override
    public void erect() {
        serverManager.startServer();
        new H2SchemaInitializer(dataSource).initialize();
    }

    @Override
    public void retract() {
        final LaunchTelemetryDataSource telemetryDataSource =
                new H2BackedLaunchTelemetryDataSource(dataSource);
        new ReportingHelper().report(telemetryDataSource);
        serverManager.stopServer();
    }
}
