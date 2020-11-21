package com.github.nagyesta.abortmission.core.telemetry;

import com.github.nagyesta.abortmission.core.MissionControl;
import com.github.nagyesta.abortmission.core.telemetry.converter.LaunchTelemetryConverter;
import com.github.nagyesta.abortmission.core.telemetry.stats.LaunchTelemetry;
import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReportingHelperTest {

    @Test
    void testReportingRootShouldReturnSystemPropertyValue() {
        //given
        final ReportingHelper underTest = new ReportingHelper();

        //when
        final Optional<String> actual = underTest.reportingRoot();

        //then
        Assertions.assertTrue(actual.isPresent());
        Assertions.assertEquals(System.getProperty(MissionControl.ABORT_MISSION_REPORT_DIRECTORY), actual.get());
    }

    @Test
    void testTestExecutionFinishedShouldSaveProvidedData() throws IOException {
        //given
        final ReportingHelper underTest = spy(new ReportingHelper());
        final String dirname = System.getProperty("java.io.tmpdir") + "/abort-mission/";
        doReturn(Optional.of(dirname)).when(underTest).reportingRoot();
        final LaunchTelemetry telemetry = new LaunchTelemetry(new LaunchTelemetryConverter(), Collections.emptyMap());
        doReturn(telemetry).when(underTest).launchTelemetry();
        final ArgumentCaptor<File> fileCaptor = ArgumentCaptor.forClass(File.class);
        doCallRealMethod().when(underTest).writeJson(any(LaunchTelemetry.class), fileCaptor.capture());

        //when
        underTest.report();

        //then
        verify(underTest).reportingRoot();
        verify(underTest).launchTelemetry();
        final File file = fileCaptor.getValue();
        Files.readAllLines(file.toPath())
                .forEach(line -> Assertions.assertEquals(new Gson().toJson(telemetry), line));
        Assertions.assertTrue(file.delete());
    }

    @Test
    void testWriteShouldCatchExceptionsWhenThrown() {
        //given
        final ReportingHelper underTest = new ReportingHelper();
        final LaunchTelemetry telemetry = new LaunchTelemetry(new LaunchTelemetryConverter(), Collections.emptyMap());

        //when
        underTest.writeJson(telemetry, null);

        //then no exception
    }
}
