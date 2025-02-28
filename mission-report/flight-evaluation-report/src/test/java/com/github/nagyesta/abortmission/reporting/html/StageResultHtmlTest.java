package com.github.nagyesta.abortmission.reporting.html;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StageResultHtmlTest {

    @Test
    void testLowerCaseNameShouldReturnTheLowerCaseVersionWhenCalled() {
        //given

        //when
        final var actual = StageResultHtml.FAILURE.lowerCaseName();

        //then
        assertEquals("failure", actual);
    }
}
