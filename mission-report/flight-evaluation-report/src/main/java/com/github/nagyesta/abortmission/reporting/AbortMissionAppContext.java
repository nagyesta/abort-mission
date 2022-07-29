package com.github.nagyesta.abortmission.reporting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nagyesta.abortmission.reporting.config.ConversionProperties;
import com.github.nagyesta.abortmission.reporting.controller.ConversionController;
import com.github.nagyesta.abortmission.reporting.html.converter.ClassJsonToHtmlConverter;
import com.github.nagyesta.abortmission.reporting.html.converter.LaunchJsonToHtmlConverter;
import com.github.nagyesta.abortmission.reporting.html.converter.StageLaunchStatsJsonToHtmlConverter;
import com.github.nagyesta.abortmission.reporting.html.converter.StatsJsonToHtmlConverter;
import org.thymeleaf.TemplateEngine;

public class AbortMissionAppContext {
    private final ConversionController controller;

    public AbortMissionAppContext(final ConversionProperties properties) {
        final ObjectMapper objectMapper = new ObjectMapper();
        final StatsJsonToHtmlConverter statsConverter = new StatsJsonToHtmlConverter();
        final StageLaunchStatsJsonToHtmlConverter stageConverter = new StageLaunchStatsJsonToHtmlConverter(statsConverter);
        final ClassJsonToHtmlConverter classConverter = new ClassJsonToHtmlConverter(statsConverter, stageConverter);
        final LaunchJsonToHtmlConverter launchConverter = new LaunchJsonToHtmlConverter(statsConverter, classConverter);
        final TemplateEngine templateEngine = new TemplateEngine();
        controller = new ConversionController(properties, objectMapper, launchConverter, templateEngine);
    }

    /**
     * Returns the controller.
     *
     * @return controller
     */
    public ConversionController controller() {
        return controller;
    }

    /**
     * Exits with an error code (1).
     */
    public void exitWithError() {
        System.exit(1);
    }
}
