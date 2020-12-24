package com.github.nagyesta.abortmission.booster.cucumber.matcher;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class ScenarioUriMatcherTest {

    private static final String PATTERN = "PATTERN";

    @Test
    void testGetPatternShouldReturnTheOriginalPatternWhenCalled() {
        //given
        final ScenarioUriMatcher underTest = new ScenarioUriMatcher(PATTERN);

        //when
        final String actual = underTest.getPattern();

        //then
        Assertions.assertEquals(PATTERN, actual);
    }

    @Test
    void testMatchesShouldReturnFalseWhenCalledWithNonScenarioObjects() {
        //given
        final ScenarioUriMatcher underTest = new ScenarioUriMatcher(PATTERN);

        //when
        final boolean actual = underTest.matches(PATTERN);

        //then
        Assertions.assertFalse(actual);
    }
}
