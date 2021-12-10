package com.github.nagyesta.abortmission.reporting.html.converter;

import com.github.nagyesta.abortmission.reporting.html.ClassHtml;
import com.github.nagyesta.abortmission.reporting.html.LaunchHtml;
import com.github.nagyesta.abortmission.reporting.html.StageResultHtml;
import com.github.nagyesta.abortmission.reporting.html.StatsHtml;
import com.github.nagyesta.abortmission.reporting.json.ClassJson;
import com.github.nagyesta.abortmission.reporting.json.LaunchJson;
import com.github.nagyesta.abortmission.reporting.json.StatsJson;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

class LaunchJsonToHtmlConverterTest {

    private static final String CLASS_NAME = "className";

    private static Stream<Arguments> validInputProvider() {
        final LaunchJson nullContent = json(null);
        final LaunchJson empty = json(Collections.emptyMap());
        final LaunchJson full = json(Collections.singletonMap(CLASS_NAME, new ClassJson()));
        full.setMissionStats(new StatsJson());
        full.setCountdownStats(new StatsJson());
        return Stream.<Arguments>builder()
                .add(Arguments.of(nullContent, LaunchHtml.builder()
                        .classes(Collections.emptySortedSet())
                        .stats(StatsHtml.builder().build())
                        .countdownStats(StatsHtml.builder().worstResult(StageResultHtml.SUPPRESSED).build())
                        .missionStats(StatsHtml.builder().worstResult(StageResultHtml.SUPPRESSED).build())
                        .build()))
                .add(Arguments.of(empty, LaunchHtml.builder()
                        .classes(Collections.emptySortedSet())
                        .stats(StatsHtml.builder().build())
                        .countdownStats(StatsHtml.builder().worstResult(StageResultHtml.SUPPRESSED).build())
                        .missionStats(StatsHtml.builder().worstResult(StageResultHtml.SUPPRESSED).build())
                        .build()))
                .add(Arguments.of(full, LaunchHtml.builder()
                        .classes(new TreeSet<>(Collections.singleton(ClassHtml.builder(CLASS_NAME).build())))
                        .stats(StatsHtml.builder().build())
                        .countdownStats(StatsHtml.builder().build())
                        .missionStats(StatsHtml.builder().build())
                        .build()))
                .build();
    }

    private static LaunchJson json(final Map<String, ClassJson> classes) {
        final LaunchJson nullContent = new LaunchJson();
        nullContent.setClasses(classes);
        nullContent.setStats(new StatsJson());
        return nullContent;
    }

    @ParameterizedTest
    @MethodSource("validInputProvider")
    void testConvertShouldConvertNonNullValuesWhenCalled(final LaunchJson input, final LaunchHtml expected) {
        //given
        final StatsJsonToHtmlConverter statsConverter = mock(StatsJsonToHtmlConverter.class);
        when(statsConverter.convert(same(input.getStats()))).thenReturn(expected.getStats());
        if (input.getCountdownStats() != null) {
            when(statsConverter.convert(same(input.getCountdownStats()))).thenReturn(expected.getCountdownStats());
        }
        if (input.getMissionStats() != null) {
            when(statsConverter.convert(same(input.getMissionStats()))).thenReturn(expected.getMissionStats());
        }
        when(statsConverter.convert(isNull())).thenThrow(NullPointerException.class);
        final ClassJsonToHtmlConverter classConverter = mock(ClassJsonToHtmlConverter.class);
        if (expected.getClasses() != null && !expected.getClasses().isEmpty()) {
            when(classConverter.convert(notNull())).thenReturn(expected.getClasses().first());
        }
        when(classConverter.convert(isNull())).thenThrow(NullPointerException.class);

        final LaunchJsonToHtmlConverter underTest = new LaunchJsonToHtmlConverter(statsConverter, classConverter);

        //when
        final LaunchHtml actual = underTest.convert(input);

        //then
        assertNotNull(actual);
        assertEquals(expected, actual);
        assertSame(expected.getStats(), actual.getStats());
        if (expected.getClasses() != null && !expected.getClasses().isEmpty()) {
            assertSame(expected.getClasses().first(), actual.getClasses().first());
            verify(classConverter).convert(same(input.getClasses().get(CLASS_NAME)));
        }
        verify(statsConverter).convert(same(input.getStats()));
        if (input.getCountdownStats() != null) {
            assertSame(expected.getCountdownStats(), actual.getCountdownStats());
            verify(statsConverter).convert(same(input.getCountdownStats()));
        }
        if (input.getMissionStats() != null) {
            assertSame(expected.getMissionStats(), actual.getMissionStats());
            verify(statsConverter).convert(same(input.getMissionStats()));
        }
        verify(statsConverter).convert(same(input.getStats()));

    }
}
