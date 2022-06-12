package com.github.nagyesta.abortmission.reporting.html;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class LaunchHtmlTest {

    private static Stream<Arguments> hashProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of("input", "002vmlua"))
                .add(Arguments.of(LaunchHtml.class.getName(), "005r0q71"))
                .add(Arguments.of(LaunchHtmlTest.class.getName(), "01sneltf"))
                .build();
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private static Stream<Arguments> millisFormatProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(1, "0s 1ms"))
                .add(Arguments.of(1001, "1s 1ms"))
                .add(Arguments.of(61001, "1m 1s"))
                .add(Arguments.of(3661001, "1h 1m 1s"))
                .add(Arguments.of(3600000, "1h 0m 0s"))
                .add(Arguments.of(3599999, "59m 59s"))
                .add(Arguments.of(1000, "1s 0ms"))
                .build();
    }

    @ParameterizedTest
    @MethodSource("hashProvider")
    void testShortHashShouldHashContentTo28RadixNumbersWhenCalledWithValidString(final String input, final String expected) {
        //given
        //when
        final String actual = LaunchHtml.shortHash(input);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("millisFormatProvider")
    void testFormatTimeMillisShouldOmitUnusedPartsWhenCalledWithMillis(final long millis, final String expected) {
        //given
        //when
        final String actual = LaunchHtml.formatTimeMillis(millis);

        //then
        Assertions.assertEquals(expected, actual);
    }
}
