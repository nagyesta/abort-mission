package com.github.nagyesta.abortmission.reporting.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nagyesta.abortmission.reporting.config.ConversionProperties;
import com.github.nagyesta.abortmission.reporting.exception.RenderException;
import com.github.nagyesta.abortmission.reporting.html.LaunchHtml;
import com.github.nagyesta.abortmission.reporting.html.converter.LaunchJsonToHtmlConverter;
import com.github.nagyesta.abortmission.reporting.json.LaunchJson;
import com.github.nagyesta.abortmission.reporting.json.StageResultJson;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@Component
@Slf4j
public class ConversionController {
    private static final String ROOT_NODE = "$";

    private final ConversionProperties properties;
    private final ObjectMapper objectMapper;
    private final LaunchJsonToHtmlConverter converter;
    private final SpringTemplateEngine templateEngine;

    @Autowired
    public ConversionController(final ConversionProperties properties,
                                final ObjectMapper objectMapper,
                                final LaunchJsonToHtmlConverter converter,
                                final SpringTemplateEngine templateEngine) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.converter = converter;
        this.templateEngine = templateEngine;
    }

    /**
     * Converts the input json defined for {@link ConversionProperties} bean into the html defined by the same bean.
     *
     * @throws RenderException When the conversion fails for any reason.
     */
    public void convert() throws RenderException {
        Assert.isTrue(properties.getInput().exists(), "Input file does not exist.");
        final LaunchJson json = readValidJson();
        render(prepareContext(json));
        if (properties.isFailOnError() && json.getStats().getWorstResult() == StageResultJson.FAILURE) {
            log.error("Failure detected in execution data and build is set to fail on error.");
            throw new RenderException();
        }
    }

    @SuppressWarnings("LocalCanBeFinal")
    private void render(final Context context) {
        try (FileOutputStream stream = new FileOutputStream(properties.getOutput());
             OutputStreamWriter writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8)) {
            templateEngine.process("html/launch-report", context, writer);
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            throw new RenderException();
        }
    }

    private Context prepareContext(final LaunchJson launchJson) {
        final LaunchHtml launchHtml = converter.convert(launchJson);
        final Context context = new Context();
        context.setVariable("launchModel", launchHtml);
        context.setVariable("allCss", readResource("/templates/css/all.min.css"));
        context.setVariable("allJs", readResource("/templates/js/all.min.js"));
        return context;
    }

    private String readResource(final String input) throws RenderException {
        try {
            return StreamUtils.copyToString(ConversionController.class.getResourceAsStream(input), StandardCharsets.UTF_8);
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
            throw new RenderException();
        }
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
            return objectMapper.treeToValue(rootNode, LaunchJson.class);
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
