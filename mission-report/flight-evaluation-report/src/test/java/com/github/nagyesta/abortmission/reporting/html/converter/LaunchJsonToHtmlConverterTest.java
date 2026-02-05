package com.github.nagyesta.abortmission.reporting.html.converter;

import com.github.nagyesta.abortmission.reporting.html.LaunchHtml;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class LaunchJsonToHtmlConverterTest {

    private static Stream<Arguments> hashProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of("input", "140f86aae51a"))
                .add(Arguments.of(LaunchHtml.class.getName(), "7571833edf4a"))
                .add(Arguments.of(LaunchJsonToHtmlConverterTest.class.getName(), "63a7b0fb0864"))
                .build();
    }

    @ParameterizedTest
    @MethodSource("hashProvider")
    void testShortHashShouldHashContentTo32RadixNumbersWhenCalledWithValidString(
            final String input,
            final String expected) {
        //given
        final var underTest = new LaunchJsonToHtmlConverter();

        //when
        final var actual = underTest.shortHash(input);

        //then
        Assertions.assertEquals(expected, actual);
    }
}
