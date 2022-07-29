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
    public void testApplicationContextShouldConfigureSuccessfullyWhenCalled() throws IOException {
        //given
        final AbortMissionFlightEvaluationReportApp app = new AbortMissionFlightEvaluationReportApp();
        //noinspection ConstantConditions
        final String input = getClass().getResource("/abort-mission-report.json").getFile();
        final File output = File.createTempFile("abort-mission-out", ".html");
        output.deleteOnExit();
        final List<String> args = List.of("--report.input=" + input, "--report.output=" + output);

        //when
        final AbortMissionAppContext actual = app.bootstrap(args);

        //then
        assertNotNull(actual);
    }

    @Test
    public void testApplicationContextShouldExitWithErrorWhenExecutionFails() throws IOException {
        //given
        final AbortMissionFlightEvaluationReportApp app = new AbortMissionFlightEvaluationReportApp();
        //noinspection ConstantConditions
        final File input = new File(getClass().getResource("/abort-mission-report.json").getFile());
        final File output = File.createTempFile("abort-mission-out", ".html");
        output.deleteOnExit();
        final ConversionProperties properties = ConversionProperties.builder()
                .input(input)
                .output(output)
                .build();
        final AbortMissionAppContext context = spy(new AbortMissionAppContext(properties));
        doNothing().when(context).exitWithError();
        doThrow(new RenderException()).when(context).controller();

        //when
        app.execute(context);

        //then
        verify(context).controller();
        verify(context).exitWithError();
    }
}
