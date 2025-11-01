package com.github.nagyesta.abortmission.reporting.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nagyesta.abortmission.reporting.config.ConversionProperties;
import com.github.nagyesta.abortmission.reporting.exception.RenderException;
import com.github.nagyesta.abortmission.reporting.html.LaunchHtml;
import com.github.nagyesta.abortmission.reporting.html.converter.LaunchJsonToHtmlConverter;
import com.github.nagyesta.abortmission.reporting.json.LaunchJson;
import com.networknt.schema.*;
import com.networknt.schema.Error;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Slf4j
public final class ConversionController {

    private final ConversionProperties properties;
    private final ObjectMapper objectMapper;
    private final LaunchJsonToHtmlConverter converter;
    private final TemplateEngine templateEngine;

    public ConversionController(final ConversionProperties properties,
                                final ObjectMapper objectMapper,
                                final LaunchJsonToHtmlConverter converter,
                                final TemplateEngine templateEngine) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.converter = converter;
        this.templateEngine = templateEngine;
        templateEngine.addDialect(new Java8TimeDialect());
        templateEngine.setTemplateResolver(new ClassLoaderTemplateResolver());
    }

    /**
     * Converts the input json defined for {@link ConversionProperties} bean into the html defined by the same bean.
     *
     * @throws RenderException When the conversion fails for any reason.
     */
    public void convert() throws RenderException {
        render(prepareContext(convertJson()));
    }

    String convertJson() {
        if (!properties.getInput().exists()) {
            throw new IllegalArgumentException("Input file does not exist.");
        }
        final var json = readValidJson();
        if (properties.isFailOnError() && json.isFailure()) {
            log.error("Failure detected in execution data and build is set to fail on error.");
            throw new RenderException();
        }
        return writeAsJson(converter.apply(json));
    }

    private String writeAsJson(final LaunchHtml launchHtml) {
        try {
            return objectMapper.writer().writeValueAsString(launchHtml);
        } catch (final JsonProcessingException e) {
            log.error("Unable to serialize launch telemetry: {}", e.getMessage(), e);
            throw new RenderException();
        }
    }

    private void render(final Context context) {
        try (var stream = new FileOutputStream(properties.getOutput());
             var writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8)) {
            templateEngine.process("/templates/html/launch-report.html", context, writer);
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            throw new RenderException();
        }
    }

    private Context prepareContext(final String launchTelemetryJson) {
        final var context = new Context();
        context.setVariable("launchModel", launchTelemetryJson);
        return context;
    }

    private LaunchJson readValidJson() throws RenderException {
        try (var stream = new FileInputStream(properties.getInput());
             var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            final var rootNode = objectMapper.readTree(reader);
            final var schema = getSchema(properties.isRelaxed());
            final var violations = schema.validate(rootNode);
            if (!violations.isEmpty()) {
                violations.stream()
                        .map(Error::getMessage)
                        .forEach(log::error);
                throw new IllegalArgumentException("validation failed.");
            }
            final var launchJson = objectMapper.treeToValue(rootNode, LaunchJson.class);
            if (launchJson.getClasses().isEmpty()) {
                log.error("No measurements found in telemetry JSON. Please double-check your reporting configuration!");
                throw new IllegalArgumentException("Telemetry has no measurements.");
            }
            return launchJson;
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            throw new RenderException();
        }
    }

    private Schema getSchema(final boolean relaxed) throws IOException {
        try (var source = getSource(relaxed)) {
            final var schemaRegistryConfig = SchemaRegistryConfig.builder().build();
            final var schemaRegistry = SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12,
                    builder -> builder.schemaRegistryConfig(schemaRegistryConfig));

            return schemaRegistry.getSchema(source, InputFormat.JSON);
        }
    }

    private InputStream getSource(final boolean relaxed) {
        if (relaxed) {
            return this.getClass().getResourceAsStream("/schema/abort-mission-telemetry-relaxed.json");
        } else {
            return this.getClass().getResourceAsStream("/schema/abort-mission-telemetry-strict.json");
        }
    }
}
