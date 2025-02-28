package com.github.nagyesta.abortmission.reporting;

import com.github.nagyesta.abortmission.reporting.exception.RenderException;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
@Slf4j
public final class AbortMissionFlightEvaluationReportApp {

    public static void main(final String[] args) throws RenderException {
        final var app = new AbortMissionFlightEvaluationReportApp();
        app.execute(app.bootstrap(Arrays.asList(args)));
    }

    AbortMissionAppContext bootstrap(final List<String> args) {
        final var properties = new PropertiesParser(args).parseArguments();
        return new AbortMissionAppContext(properties);
    }

    void execute(final AbortMissionAppContext context) {
        try {
            context.controller().convert();
        } catch (final RenderException ex) {
            log.error(ex.getMessage(), ex);
            context.exitWithError();
        }
    }

}
