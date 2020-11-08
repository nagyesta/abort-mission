package com.github.nagyesta.abortmission.booster.testng.extractor;

import org.testng.annotations.Test;

import java.util.Optional;
import java.util.Set;

import static org.testng.Assert.assertFalse;

class GroupDependencyNameExtractorTest {

    public static final String STRING = "string";

    @Test(groups = "unit")
    void testApplyShouldReturnEmptyOptionalWhenCalledWithNeitherClassNorMethod() {
        //given
        final GroupDependencyNameExtractor underTest = new GroupDependencyNameExtractor();

        //when
        final Optional<Set<String>> actual = underTest.apply(STRING);

        //then
        assertFalse(actual.isPresent());
    }
}
