package com.github.nagyesta.abortmission.booster.testng.extractor;

import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;

class GroupDependencyNameExtractorTest {

    public static final String STRING = "string";

    @Test(groups = "unit")
    void testApplyShouldReturnEmptyOptionalWhenCalledWithNeitherClassNorMethod() {
        //given
        final var underTest = new GroupDependencyNameExtractor();

        //when
        final var actual = underTest.apply(STRING);

        //then
        assertFalse(actual.isPresent());
    }
}
