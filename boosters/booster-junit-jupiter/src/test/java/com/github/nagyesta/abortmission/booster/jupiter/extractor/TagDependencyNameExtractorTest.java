package com.github.nagyesta.abortmission.booster.jupiter.extractor;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;

@Tag("unit")
class TagDependencyNameExtractorTest {

    public static final String STRING = "string";

    @Test
    void testApplyShouldReturnEmptyOptionalWhenCalledWithNeitherClassNorMethod() {
        //given
        final TagDependencyNameExtractor underTest = new TagDependencyNameExtractor();

        //when
        final Optional<Set<String>> actual = underTest.apply(STRING);

        //then
        assertFalse(actual.isPresent());
    }
}
