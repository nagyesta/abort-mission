package com.github.nagyesta.abortmission.core.telemetry;

import java.io.OutputStream;

/**
 * Allows us to export all collected telemetry to a stream.
 */
public interface TelemetryExporter {

    /**
     * Exports all telemetry from all {@link com.github.nagyesta.abortmission.core.MissionControl} namespaces to the provided stream.
     *
     * @param stream The stream we need to write to.
     */
    void exportAll(OutputStream stream);
}
