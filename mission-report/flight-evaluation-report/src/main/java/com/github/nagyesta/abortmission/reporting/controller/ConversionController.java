package com.github.nagyesta.abortmission.reporting.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nagyesta.abortmission.reporting.config.ConversionProperties;
import com.github.nagyesta.abortmission.reporting.exception.RenderException;
import com.github.nagyesta.abortmission.reporting.html.LaunchHtml;
import com.github.nagyesta.abortmission.reporting.html.converter.LaunchJsonToHtmlConverter;
import com.github.nagyesta.abortmission.reporting.json.LaunchJson;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@Slf4j
public final class ConversionController {
    private static final String ROOT_NODE = "$";

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
        final LaunchJson json = readValidJson();
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
            log.error("Unable to serialize launch telemetry: " + e.getMessage(), e);
            throw new RenderException();
        }
    }

    @SuppressWarnings("LocalCanBeFinal")
    private void render(final Context context) {
        try (FileOutputStream stream = new FileOutputStream(properties.getOutput());
             OutputStreamWriter writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8)) {
            templateEngine.process("/templates/html/launch-report.html", context, writer);
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            throw new RenderException();
        }
    }

    private Context prepareContext(final String launchTelemetryJson) {
        final Context context = new Context();
        context.setVariable("launchModel", launchTelemetryJson);
        return context;
    }

    @SuppressWarnings("LocalCanBeFinal")
    private LaunchJson readValidJson() throws RenderException {
        try (FileInputStream stream = new FileInputStream(properties.getInput());
             InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            final JsonNode rootNode = objectMapper.readTree(reader);
            final JsonSchema schema = getSchema(properties.isRelaxed());
            final Set<ValidationMessage> violations = schema.validate(rootNode, rootNode, ROOT_NODE);
            if (!violations.isEmpty()) {
                violations.stream()
                        .map(ValidationMessage::getMessage)
                        .forEach(log::error);
                throw new IllegalArgumentException("validation failed.");
            }
            final LaunchJson launchJson = objectMapper.treeToValue(rootNode, LaunchJson.class);
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

    @SuppressWarnings("LocalCanBeFinal")
    private JsonSchema getSchema(final boolean relaxed) throws IOException {
        try (InputStream source = getSource(relaxed)) {
            final JsonNode schemaNode = objectMapper.readTree(source);
            final JsonSchemaFactory factory = JsonSchemaFactory
                    .builder(JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7))
                    .objectMapper(objectMapper)
                    .build();
            return factory.getSchema(schemaNode);
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
