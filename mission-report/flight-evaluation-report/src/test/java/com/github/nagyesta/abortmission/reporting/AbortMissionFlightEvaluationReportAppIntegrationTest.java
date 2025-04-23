package com.github.nagyesta.abortmission.reporting;

import com.github.nagyesta.abortmission.reporting.config.ConversionProperties;
import com.github.nagyesta.abortmission.reporting.exception.RenderException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class AbortMissionFlightEvaluationReportAppIntegrationTest {

    @Test
    void testApplicationContextShouldConfigureSuccessfullyWhenCalled() throws IOException {
        //given
        final var app = new AbortMissionFlightEvaluationReportApp();
        //noinspection ConstantConditions
        final var input = getClass().getResource("/abort-mission-report.json").getFile();
        final var output = File.createTempFile("abort-mission-out", ".html");
        output.deleteOnExit();
        final var args = List.of("--report.input=" + input, "--report.output=" + output);

        //when
        final var actual = app.bootstrap(args);

        //then
        assertNotNull(actual);
    }

    @Test
    void testApplicationContextShouldExitWithErrorWhenExecutionFails() throws IOException {
        //given
        final var app = new AbortMissionFlightEvaluationReportApp();
        //noinspection ConstantConditions
        final var input = new File(getClass().getResource("/abort-mission-report.json").getFile());
        final var output = File.createTempFile("abort-mission-out", ".html");
        output.deleteOnExit();
        final var properties = ConversionProperties.builder()
                .input(input)
                .output(output)
                .build();
        final var context = spy(new AbortMissionAppContext(properties));
        doNothing().when(context).exitWithError();
        doThrow(new RenderException()).when(context).controller();

        //when
        app.execute(context);

        //then
        verify(context).controller();
        verify(context).exitWithError();
    }
}
