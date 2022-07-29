package com.github.nagyesta.abortmission.reporting.html.converter;

import com.github.nagyesta.abortmission.reporting.html.LaunchHtml;
import com.github.nagyesta.abortmission.reporting.html.StageLaunchStatsHtml;
import com.github.nagyesta.abortmission.reporting.html.StatsHtml;
import com.github.nagyesta.abortmission.reporting.json.StageLaunchStatsJson;
import com.github.nagyesta.abortmission.reporting.json.StatsJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StageLaunchStatsJsonToHtmlConverterTest {

    private static final String METHOD_NAME = "methodName";
    private static final String MATCHER_NAME = "matcherName";

    private static Stream<Arguments> validInputProvider() {
        final StageLaunchStatsJson empty = new StageLaunchStatsJson();
        empty.setStats(new StatsJson());
        final StageLaunchStatsJson withMap = new StageLaunchStatsJson();
        withMap.setStats(new StatsJson());
        withMap.setMatcherNames(new TreeSet<>(Collections.singleton(MATCHER_NAME)));
        return Stream.<Arguments>builder()
                .add(Arguments.of(empty, StageLaunchStatsHtml.builder(METHOD_NAME, StatsHtml.builder().build())
                        .titleName(METHOD_NAME.toLowerCase())
                        .matcherNames(Collections.emptySortedMap())
                        .build()))
                .add(Arguments.of(withMap, StageLaunchStatsHtml.builder(METHOD_NAME, StatsHtml.builder().build())
                        .titleName(METHOD_NAME.toLowerCase())
                        .matcherNames(new TreeMap<>(Collections.singletonMap(MATCHER_NAME, LaunchHtml.shortHash(MATCHER_NAME))))
                        .build()))
                .build();
    }

    @ParameterizedTest
    @MethodSource("validInputProvider")
    void testConvertShouldConvertNonNullValuesWhenCalled(final StageLaunchStatsJson input, final StageLaunchStatsHtml expected) {
        //given
        final StatsJsonToHtmlConverter statsConverter = mock(StatsJsonToHtmlConverter.class);
        when(statsConverter.apply(notNull())).thenReturn(expected.getStats());
        when(statsConverter.apply(isNull())).thenThrow(new NullPointerException());

        final StageLaunchStatsJsonToHtmlConverter underTest = new StageLaunchStatsJsonToHtmlConverter(statsConverter);

        //when
        final StageLaunchStatsHtml actual = underTest.apply(String::toLowerCase, METHOD_NAME, input);

        //then
        assertNotNull(actual);
        assertEquals(expected, actual);
        assertSame(expected.getStats(), actual.getStats());
        verify(statsConverter).apply(same(input.getStats()));
    }

    @Test
    void testConvertShouldThrowExceptionWhenCalledWithNullStats() {
        //given
        final StageLaunchStatsJson input = new StageLaunchStatsJson();
        final StatsJsonToHtmlConverter statsConverter = mock(StatsJsonToHtmlConverter.class);

        final StageLaunchStatsJsonToHtmlConverter underTest = new StageLaunchStatsJsonToHtmlConverter(statsConverter);

        //when
        Assertions.assertThrows(IllegalArgumentException.class, () -> underTest.apply(Function.identity(), "name", input));

        //then + exception
        verifyNoInteractions(statsConverter);
    }
}
