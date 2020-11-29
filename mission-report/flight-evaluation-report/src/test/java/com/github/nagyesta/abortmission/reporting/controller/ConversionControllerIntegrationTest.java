package com.github.nagyesta.abortmission.reporting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nagyesta.abortmission.reporting.config.ConversionProperties;
import com.github.nagyesta.abortmission.reporting.html.converter.LaunchJsonToHtmlConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ConversionControllerIntegrationTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private LaunchJsonToHtmlConverter converter;
    @Autowired
    private SpringTemplateEngine templateEngine;

    private static Stream<Arguments> validInputSource() {
        return Stream.<Arguments>builder()
                .add(Arguments.of("/abort-mission-report.json", true, "/abort-mission-report.html"))
                .add(Arguments.of("/abort-mission-report.json", false, "/abort-mission-report.html"))
                .build();
    }

    @ParameterizedTest
    @MethodSource("validInputSource")
    void testConvertShouldConvertAndRenderDataWhenCalledWithValidJson(final String jsonResource,
                                                                      final boolean relaxed,
                                                                      final String expectedHtml) throws Exception {
        //given
        final File inputFile = new File(this.getClass().getResource(jsonResource).getFile());
        final ConversionProperties properties = new ConversionProperties();
        properties.setInput(inputFile);
        properties.setOutput(File.createTempFile("abort-mission-test", ".html"));
        properties.setRelaxed(relaxed);

        properties.getOutput().deleteOnExit();

        final ConversionController underTest = new ConversionController(properties, objectMapper, converter, templateEngine);

        //when
        underTest.convert();

        //then
        final List<String> actualLines = Files.readAllLines(properties.getOutput().toPath());
        final List<String> expectedLines = Files.readAllLines(new File(this.getClass().getResource(expectedHtml).getFile()).toPath());
        assertIterableEquals(expectedLines, actualLines);
    }

    @Test
    void testConvertShouldThrowExceptionWhenCalledWithInvalidJson() throws Exception {
        //given
        final File inputFile = new File(this.getClass().getResource("/schema/abort-mission-telemetry-relaxed.json").getFile());
        final ConversionProperties properties = new ConversionProperties();
        properties.setInput(inputFile);
        properties.setOutput(File.createTempFile("abort-mission-test", ".html"));
        properties.setRelaxed(false);

        properties.getOutput().deleteOnExit();

        final ConversionController underTest = new ConversionController(properties, objectMapper, converter, templateEngine);

        //when
        assertThrows(RuntimeException.class, underTest::convert);

        //then + exception
    }
}
