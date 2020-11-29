package com.github.nagyesta.abortmission.reporting.html;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StageLaunchStatsHtmlTest {

    private static Stream<Arguments> statusProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(StageResultHtml.SUCCESS, true))
                .add(Arguments.of(StageResultHtml.FAILURE, false))
                .add(Arguments.of(StageResultHtml.SUPPRESSED, true))
                .add(Arguments.of(StageResultHtml.ABORT, false))
                .build();
    }

    @ParameterizedTest
    @MethodSource("statusProvider")
    void testIsCollapsedShouldReturnTrueOnlyWhenCalledForSuccessOrSuppressed(final StageResultHtml input, final boolean expected) {
        //given
        final StageLaunchStatsHtml underTest = StageLaunchStatsHtml.builder()
                .displayName(input.lowerCaseName())
                .stats(StatsHtml.builder().worstResult(input).build())
                .build();

        //when
        final boolean actual = underTest.isCollapsed();

        //then
        assertEquals(expected, actual);
    }
}
