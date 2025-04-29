package com.github.nagyesta.abortmission.booster.jupiter.extractor;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

@Tag("unit")
class TagDependencyNameExtractorTest {

    private static final String STRING = "string";

    @Test
    void testApplyShouldReturnEmptyOptionalWhenCalledWithNeitherClassNorMethod() {
        //given
        final var underTest = new TagDependencyNameExtractor();

        //when
        final var actual = underTest.apply(STRING);

        //then
        assertFalse(actual.isPresent());
    }
}
