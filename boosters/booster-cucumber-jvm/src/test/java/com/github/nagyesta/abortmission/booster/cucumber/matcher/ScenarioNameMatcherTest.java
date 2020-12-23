package com.github.nagyesta.abortmission.booster.cucumber.matcher;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class ScenarioNameMatcherTest {

    private static final String PATTERN = "PATTERN";

    @Test
    void testGetPatternShouldReturnTheOriginalPatternWhenCalled() {
        //given
        final ScenarioNameMatcher underTest = new ScenarioNameMatcher(PATTERN);

        //when
        final String actual = underTest.getPattern();

        //then
        Assertions.assertEquals(PATTERN, actual);
    }

    @Test
    void testMatchesShouldReturnFalseWhenCalledWithNonScenarioObjects() {
        //given
        final ScenarioNameMatcher underTest = new ScenarioNameMatcher(PATTERN);

        //when
        final boolean actual = underTest.matches(PATTERN);

        //then
        Assertions.assertFalse(actual);
    }
}
