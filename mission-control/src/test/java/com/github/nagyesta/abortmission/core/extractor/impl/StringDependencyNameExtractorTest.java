package com.github.nagyesta.abortmission.core.extractor.impl;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class StringDependencyNameExtractorTest {

    private static final String A = "a";
    private static final String EMPTY = "";
    private static final int INT_42 = 42;
    private final StringDependencyNameExtractor underTest = new StringDependencyNameExtractor();

    private static Stream<Arguments> dependencyConversionProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, null))
                .add(Arguments.of(INT_42, null))
                .add(Arguments.of(EMPTY, null))
                .add(Arguments.of(A, Collections.singleton(A)))
                .build();
    }

    @ParameterizedTest
    @MethodSource("dependencyConversionProvider")
    void testApplyShouldFilterAndCastInputWhenCalled(
            final Object input,
            final Set<String> expected) {
        //given

        //when
        final var actual = underTest.apply(input);

        //then
        if (expected == null) {
            assertFalse(actual.isPresent());
        } else {
            assertTrue(actual.isPresent());
            assertIterableEquals(expected, actual.get());
        }
    }
}
