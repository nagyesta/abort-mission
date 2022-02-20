package com.github.nagyesta.abortmission.core.telemetry;

import com.github.nagyesta.abortmission.core.telemetry.stats.DefaultLaunchTelemetry;
import com.github.nagyesta.abortmission.core.telemetry.stats.DefaultLaunchTelemetryDataSource;
import com.github.nagyesta.abortmission.core.telemetry.stats.LaunchTelemetry;
import com.github.nagyesta.abortmission.core.telemetry.stats.LaunchTelemetryDataSource;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.github.nagyesta.abortmission.core.MissionControl.ABORT_MISSION_REPORT_DIRECTORY;

/**
 * Helper class taking the responsibility of collecting and writing the JSON test report to the file system.
 */
public class ReportingHelper {

    private static final String ABORT_MISSION_REPORT_JSON = "abort-mission-report.json";

    /**
     * Primary entry point of the class. Collects telemetry and saves to the designated folder.
     */
    public void report() {
        report(launchTelemetryDataSource());
    }

    /**
     * Collects telemetry and saves to the designated folder using the provided data source as input.
     *
     * @param dataSource The source of the telemetry data.
     */
    public void report(final LaunchTelemetryDataSource dataSource) {
        reportingRoot().ifPresent(r -> {
            final LaunchTelemetry telemetry = launchTelemetry(dataSource);
            final File json = jsonFile(r);
            writeJson(telemetry, json);
        });
    }

    /**
     * Returns ths collected launch telemetry data as a data source.
     *
     * @return telemetry
     */
    protected LaunchTelemetryDataSource launchTelemetryDataSource() {
        return DefaultLaunchTelemetryDataSource.collect();
    }

    private LaunchTelemetry launchTelemetry(final LaunchTelemetryDataSource dataSource) {
        return new DefaultLaunchTelemetry(dataSource);
    }

    /**
     * Returns the root of the reporting hierarchy.
     *
     * @return the reporting root
     */
    protected Optional<String> reportingRoot() {
        return Optional.ofNullable(System.getProperty(ABORT_MISSION_REPORT_DIRECTORY))
                .map(String::trim)
                .filter(s -> !s.isEmpty());
    }

    /**
     * Writes the launch telemetry data into the provided json file.
     *
     * @param telemetry The telemetry we need to write.
     * @param json      The json file target.
     */
    @SuppressWarnings("LocalCanBeFinal")
    protected void writeJson(final LaunchTelemetry telemetry, final File json) {
        try (FileOutputStream stream = new FileOutputStream(json);
             OutputStreamWriter jsonWriter = new OutputStreamWriter(stream, StandardCharsets.UTF_8)) {
            final String jsonReport = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .create().toJson(telemetry);
            jsonWriter.write(jsonReport);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the report directory and generates the json file name.
     *
     * @param reportDir The path of the report directory
     * @return the json files descriptor.
     */
    protected File jsonFile(final String reportDir) {
        final File reportRoot = new File(reportDir);
        //noinspection ResultOfMethodCallIgnored
        reportRoot.mkdirs();
        return new File(reportRoot, ABORT_MISSION_REPORT_JSON);
    }
}
