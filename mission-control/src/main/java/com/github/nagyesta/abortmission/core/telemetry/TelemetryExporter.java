package com.github.nagyesta.abortmission.core.telemetry;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Allows us to export all collected telemetry to a stream.
 */
public interface TelemetryExporter {

    /**
     * Exports all telemetry from all {@link com.github.nagyesta.abortmission.core.MissionControl} namespaces to the provided stream.
     *
     * @param stream The stream we need to write to.
     * @throws IOException When the export operation is not possible due to IO issues.
     */
    void exportAll(OutputStream stream) throws IOException;
}
