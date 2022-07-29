package com.github.nagyesta.abortmission.reporting.html.converter;

import com.github.nagyesta.abortmission.reporting.html.ClassHtml;
import com.github.nagyesta.abortmission.reporting.html.StageLaunchStatsHtml;
import com.github.nagyesta.abortmission.reporting.html.StatsHtml;
import com.github.nagyesta.abortmission.reporting.json.ClassJson;
import com.github.nagyesta.abortmission.reporting.json.StageLaunchStatsJson;
import com.github.nagyesta.abortmission.reporting.json.StatsJson;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClassJsonToHtmlConverterTest {

    private static final String COUNTDOWN = "Countdown";
    private static final String CLASS_NAME = ClassJsonToHtmlConverterTest.class.getName();
    private static final String METHOD_NAME = "methodName";

    private static Stream<Arguments> validInputProvider() {
        final ClassJson full = json(Collections.singletonMap(METHOD_NAME, new StageLaunchStatsJson()));
        full.setCountdown(new StageLaunchStatsJson());
        return Stream.<Arguments>builder()
                .add(Arguments.of(json(null), ClassHtml.builder(CLASS_NAME)
                        .stats(StatsHtml.builder().build())
                        .build()))
                .add(Arguments.of(json(Collections.emptyMap()), ClassHtml.builder(CLASS_NAME)
                        .launches(Collections.emptyMap())
                        .stats(StatsHtml.builder().build())
                        .build()))
                .add(Arguments.of(full, ClassHtml.builder(CLASS_NAME)
                        .countdown(StageLaunchStatsHtml.builder(COUNTDOWN, StatsHtml.builder().build())
                                .build())
                        .launches(Collections.singletonMap(METHOD_NAME,
                                StageLaunchStatsHtml.builder(METHOD_NAME, StatsHtml.builder().build())
                                        .build()))
                        .stats(StatsHtml.builder().build())
                        .build()))
                .build();
    }

    private static ClassJson json(final Map<String, StageLaunchStatsJson> launches) {
        final ClassJson nullContent = new ClassJson();
        nullContent.setClassName(CLASS_NAME);
        nullContent.setLaunches(launches);
        nullContent.setStats(new StatsJson());
        return nullContent;
    }

    private static Stream<Arguments> methodNameProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of("", ""))
                .add(Arguments.of("test", "test"))
                .add(Arguments.of("testShouldWhenThen", "testShouldWhenThen"))
                .add(Arguments.of("testWhatShouldDoWhatWhenCondition", "test\nWhat\nShould\nDoWhat\nWhen\nCondition"))
                .build();
    }

    @ParameterizedTest
    @MethodSource("validInputProvider")
    void testConvertShouldConvertNonNullValuesWhenCalled(final ClassJson input, final ClassHtml expected) {
        //given
        final StatsJsonToHtmlConverter statsConverter = mock(StatsJsonToHtmlConverter.class);
        when(statsConverter.apply(notNull())).thenReturn(expected.getStats());
        when(statsConverter.apply(isNull())).thenThrow(new NullPointerException());

        final StageLaunchStatsJsonToHtmlConverter stageConverter = mock(StageLaunchStatsJsonToHtmlConverter.class);
        when(stageConverter.apply(any(), eq(COUNTDOWN), notNull())).thenReturn(expected.getCountdown());
        when(stageConverter.apply(any(), anyString(), isNull())).thenThrow(new NullPointerException());
        final Optional<StageLaunchStatsHtml> optionalLaunch = Optional.ofNullable(expected.getLaunches())
                .map(Map::values)
                .map(Collection::stream)
                .flatMap(Stream::findFirst);
        optionalLaunch.ifPresent(o ->
                when(stageConverter.apply(any(), eq(METHOD_NAME), any(StageLaunchStatsJson.class)))
                        .thenReturn(o));

        final ClassJsonToHtmlConverter underTest = new ClassJsonToHtmlConverter(statsConverter, stageConverter);

        //when
        final ClassHtml actual = underTest.apply(input);

        //then
        assertNotNull(actual);
        assertEquals(expected, actual);
        assertSame(expected.getStats(), actual.getStats());
        assertSame(expected.getCountdown(), actual.getCountdown());
        if (expected.getLaunches() != null) {
            expected.getLaunches().forEach((k, v) -> {
                assertSame(v, actual.getLaunches().get(k));
                verify(stageConverter).apply(any(), eq(METHOD_NAME), same(input.getLaunches().get(k)));
            });
        }
        verify(statsConverter).apply(eq(input.getStats()));
        if (input.getCountdown() != null) {
            verify(stageConverter).apply(any(), eq(COUNTDOWN), same(input.getCountdown()));
        }
    }

    @ParameterizedTest
    @MethodSource("methodNameProvider")
    void testConvertTestMethodShouldConvertToTooltipWhenCalledWithMatchingMethodNames(final String input, final String expected) {
        //given
        final StatsJsonToHtmlConverter statsConverter = mock(StatsJsonToHtmlConverter.class);
        final StageLaunchStatsJsonToHtmlConverter stageConverter = mock(StageLaunchStatsJsonToHtmlConverter.class);
        final ClassJsonToHtmlConverter underTest = new ClassJsonToHtmlConverter(statsConverter, stageConverter);

        //when
        final String actual = underTest.convertTestMethod(input);

        //then
        assertEquals(expected, actual);
    }
}
