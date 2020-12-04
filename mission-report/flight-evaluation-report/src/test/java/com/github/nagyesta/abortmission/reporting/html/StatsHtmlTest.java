package com.github.nagyesta.abortmission.reporting.html;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("checkstyle:MagicNumber")
class StatsHtmlTest {

    private static final String CLASS_NAME = "class";
    private static final String EMPTY = "";

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void testCssIfSuccessInactiveShouldReturnInputCssUnchangedWhenZeroOnly(final int count) {
        //given
        final StatsHtml underTest = StatsHtml.builder().success(count).build();

        //when
        final String actual = underTest.cssIfSuccessInactive(CLASS_NAME);

        //then
        if (count == 0) {
            assertEquals(CLASS_NAME, actual);
        } else {
            assertEquals(EMPTY, actual);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void testCssIfFailureInactiveShouldReturnInputCssUnchangedWhenZeroOnly(final int count) {
        //given
        final StatsHtml underTest = StatsHtml.builder().failure(count).build();

        //when
        final String actual = underTest.cssIfFailureInactive(CLASS_NAME);

        //then
        if (count == 0) {
            assertEquals(CLASS_NAME, actual);
        } else {
            assertEquals(EMPTY, actual);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void testCssIfAbortInactiveShouldReturnInputCssUnchangedWhenZeroOnly(final int count) {
        //given
        final StatsHtml underTest = StatsHtml.builder().abort(count).build();

        //when
        final String actual = underTest.cssIfAbortInactive(CLASS_NAME);

        //then
        if (count == 0) {
            assertEquals(CLASS_NAME, actual);
        } else {
            assertEquals(EMPTY, actual);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void testCssIfSuppressedInactiveShouldReturnInputCssUnchangedWhenZeroOnly(final int count) {
        //given
        final StatsHtml underTest = StatsHtml.builder().suppressed(count).build();

        //when
        final String actual = underTest.cssIfSuppressedInactive(CLASS_NAME);

        //then
        if (count == 0) {
            assertEquals(CLASS_NAME, actual);
        } else {
            assertEquals(EMPTY, actual);
        }
    }
}
