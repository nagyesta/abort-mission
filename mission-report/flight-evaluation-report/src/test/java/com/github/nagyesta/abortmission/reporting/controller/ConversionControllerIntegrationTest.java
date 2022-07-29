package com.github.nagyesta.abortmission.reporting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nagyesta.abortmission.reporting.config.ConversionProperties;
import com.github.nagyesta.abortmission.reporting.exception.RenderException;
import com.github.nagyesta.abortmission.reporting.html.converter.ClassJsonToHtmlConverter;
import com.github.nagyesta.abortmission.reporting.html.converter.LaunchJsonToHtmlConverter;
import com.github.nagyesta.abortmission.reporting.html.converter.StageLaunchStatsJsonToHtmlConverter;
import com.github.nagyesta.abortmission.reporting.html.converter.StatsJsonToHtmlConverter;
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
        final StatsJsonToHtmlConverter statsConverter = new StatsJsonToHtmlConverter();
        final StageLaunchStatsJsonToHtmlConverter stageConverter = new StageLaunchStatsJsonToHtmlConverter(statsConverter);
        final ClassJsonToHtmlConverter classConverter = new ClassJsonToHtmlConverter(statsConverter, stageConverter);
        templateEngine = new TemplateEngine();
        launchConverter = new LaunchJsonToHtmlConverter(statsConverter, classConverter);
        objectMapper = new ObjectMapper();
    }

    private static Stream<Arguments> validInputSource() {
        return Stream.<Arguments>builder()
                .add(Arguments.of("/abort-mission-report.json", true, true, "/abort-mission-report.txt"))
                .add(Arguments.of("/abort-mission-report.json", true, false, "/abort-mission-report.txt"))
                .add(Arguments.of("/abort-mission-report.json", false, true, "/abort-mission-report.txt"))
                .add(Arguments.of("/abort-mission-report.json", false, false, "/abort-mission-report.txt"))
                .build();
    }

    @ParameterizedTest
    @MethodSource("validInputSource")
    void testConvertShouldConvertAndRenderDataWhenCalledWithValidJson(final String jsonResource,
                                                                      final boolean relaxed,
                                                                      final boolean failOnError,
                                                                      final String expectedHtml) throws Exception {
        //given
        final File inputFile = new File(this.getClass().getResource(jsonResource).getFile());
        final File expectedFile = new File(this.getClass().getResource(expectedHtml).getFile());
        final ConversionProperties properties = ConversionProperties.builder()
                .input(inputFile)
                .output(File.createTempFile("abort-mission-test", ".html"))
                .relaxed(relaxed)
                .failOnError(failOnError)
                .build();

        properties.getOutput().deleteOnExit();

        final ConversionController underTest = new ConversionController(properties, objectMapper, launchConverter, templateEngine);

        //when
        if (failOnError) {
            assertThrows(RenderException.class, underTest::convert);
        } else {
            underTest.convert();
        }

        //then
        final List<String> actualLines = Files.readAllLines(properties.getOutput().toPath(), StandardCharsets.UTF_8);
        final List<String> expectedLines = Files.readAllLines(expectedFile.toPath(), StandardCharsets.UTF_8);
        assertIterableEquals(expectedLines, actualLines);
    }

    @Test
    void testConvertShouldThrowExceptionWhenCalledWithInvalidJson() throws Exception {
        //given
        final File inputFile = new File(this.getClass().getResource("/schema/abort-mission-telemetry-relaxed.json").getFile());
        final ConversionProperties properties = ConversionProperties.builder()
                .input(inputFile)
                .output(File.createTempFile("abort-mission-test", ".html"))
                .relaxed(false)
                .build();

        properties.getOutput().deleteOnExit();

        final ConversionController underTest = new ConversionController(properties, objectMapper, launchConverter, templateEngine);

        //when
        assertThrows(RuntimeException.class, underTest::convert);

        //then + exception
    }
}
