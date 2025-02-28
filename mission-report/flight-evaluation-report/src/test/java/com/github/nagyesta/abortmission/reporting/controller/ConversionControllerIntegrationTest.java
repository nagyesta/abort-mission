package com.github.nagyesta.abortmission.reporting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nagyesta.abortmission.reporting.config.ConversionProperties;
import com.github.nagyesta.abortmission.reporting.exception.RenderException;
import com.github.nagyesta.abortmission.reporting.html.converter.LaunchJsonToHtmlConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.thymeleaf.TemplateEngine;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConversionControllerIntegrationTest {

    private final ObjectMapper objectMapper;
    private final LaunchJsonToHtmlConverter launchConverter;
    private final TemplateEngine templateEngine;

    ConversionControllerIntegrationTest() {
        templateEngine = new TemplateEngine();
        launchConverter = new LaunchJsonToHtmlConverter();
        objectMapper = new ObjectMapper();
    }

    private static Stream<Arguments> validInputSource() {
        return Stream.<Arguments>builder()
                .add(Arguments.of("/abort-mission-report.json", true, true, "/abort-mission-report-html-json.txt"))
                .add(Arguments.of("/abort-mission-report.json", true, false, "/abort-mission-report-html-json.txt"))
                .add(Arguments.of("/abort-mission-report.json", false, true, "/abort-mission-report-html-json.txt"))
                .add(Arguments.of("/abort-mission-report.json", false, false, "/abort-mission-report-html-json.txt"))
                .add(Arguments.of("/abort-mission-report-cucumber.json", true, false, "/abort-mission-report-cucumber-html-json.txt"))
                .build();
    }

    @ParameterizedTest
    @MethodSource("validInputSource")
    void testConvertJsonShouldConvertJsonDataWhenCalledWithValidJson(final String jsonResource,
                                                                     final boolean relaxed,
                                                                     final boolean failOnError,
                                                                     final String expectedHtml) throws Exception {
        //given
        final var inputFile = new File(this.getClass().getResource(jsonResource).getFile());
        final var expectedFile = new File(this.getClass().getResource(expectedHtml).getFile());
        final var properties = ConversionProperties.builder()
                .input(inputFile)
                .output(File.createTempFile("abort-mission-test", ".html"))
                .relaxed(relaxed)
                .failOnError(failOnError)
                .build();

        properties.getOutput().deleteOnExit();

        final var underTest = new ConversionController(properties, objectMapper, launchConverter, templateEngine);

        //when
        if (failOnError) {
            assertThrows(RenderException.class, underTest::convert);
        } else {
            final var actual = underTest.convertJson();

            //then
            final var actualLines = List.of(actual.split("\n"));
            final var expectedLines = Files.readAllLines(expectedFile.toPath(), StandardCharsets.UTF_8);
            assertIterableEquals(expectedLines, actualLines);
        }
    }

    @Test
    void testConvertJsonShouldThrowExceptionWhenCalledWithEmptyJson() throws Exception {
        //given
        final var inputFile = new File(this.getClass().getResource("/abort-mission-report-empty.json").getFile());
        final var properties = ConversionProperties.builder()
                .input(inputFile)
                .output(File.createTempFile("abort-mission-test", ".html"))
                .relaxed(false)
                .build();

        properties.getOutput().deleteOnExit();

        final var underTest = new ConversionController(properties, objectMapper, launchConverter, templateEngine);

        //when
        assertThrows(RenderException.class, underTest::convertJson);

        //then + exception
    }

    @Test
    void testConvertShouldThrowExceptionWhenCalledWithInvalidJson() throws Exception {
        //given
        final var inputFile = new File(this.getClass().getResource("/schema/abort-mission-telemetry-relaxed.json").getFile());
        final var properties = ConversionProperties.builder()
                .input(inputFile)
                .output(File.createTempFile("abort-mission-test", ".html"))
                .relaxed(false)
                .build();

        properties.getOutput().deleteOnExit();

        final var underTest = new ConversionController(properties, objectMapper, launchConverter, templateEngine);

        //when
        assertThrows(RuntimeException.class, underTest::convert);

        //then + exception
    }

    @Test
    void testConvertShouldThrowExceptionWhenCalledWithNonExistingInputJson() throws Exception {
        //given
        final var inputFile = new File("/not-a-real-path.json");
        final var properties = ConversionProperties.builder()
                .input(inputFile)
                .output(File.createTempFile("abort-mission-test", ".html"))
                .relaxed(false)
                .build();

        properties.getOutput().deleteOnExit();

        final var underTest = new ConversionController(properties, objectMapper, launchConverter, templateEngine);

        //when
        assertThrows(RuntimeException.class, underTest::convert);

        //then + exception
    }
}
