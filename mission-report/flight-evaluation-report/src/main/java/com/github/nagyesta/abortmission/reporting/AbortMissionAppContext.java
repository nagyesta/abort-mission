package com.github.nagyesta.abortmission.reporting;

import com.github.nagyesta.abortmission.reporting.config.ConversionProperties;
import com.github.nagyesta.abortmission.reporting.controller.ConversionController;
import com.github.nagyesta.abortmission.reporting.html.converter.LaunchJsonToHtmlConverter;
import org.thymeleaf.TemplateEngine;
import tools.jackson.databind.ObjectMapper;

public class AbortMissionAppContext {
    private final ConversionController controller;

    public AbortMissionAppContext(final ConversionProperties properties) {
        final var objectMapper = new ObjectMapper();
        final var launchConverter = new LaunchJsonToHtmlConverter();
        final var templateEngine = new TemplateEngine();
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
