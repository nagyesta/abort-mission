package com.github.nagyesta.abortmission.reporting.html;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClassHtmlTest {

    private static Stream<Arguments> classNameProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of("ClassName", "ClassName"))
                .add(Arguments.of("classpath:com/github/nagyesta/abortmission/Example.feature", "co/gi/na/ab/Example"))
                .add(Arguments.of(String.class.getName(), "ja.la.String"))
                .add(Arguments.of(Collectors.class.getName(), "ja.ut.st.Collectors"))
                .add(Arguments.of(ClassHtml.ClassHtmlBuilder.class.getName(), "co.gi.na.ab.re.ht.ClassHtml$ClassHtmlBuilder"))
                .build();
    }

    private static Stream<Arguments> statusProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(StageResultHtml.SUCCESS, true))
                .add(Arguments.of(StageResultHtml.FAILURE, false))
                .add(Arguments.of(StageResultHtml.SUPPRESSED, false))
                .add(Arguments.of(StageResultHtml.ABORT, false))
                .build();
    }

    @ParameterizedTest
    @MethodSource("classNameProvider")
    void testGetClassNameTextShortShouldKeepOnlyTwoCharacterPrefixesOfPackagesWhenCalledWithValidClassNames(
            final String className, final String expected) {
        //given
        final ClassHtml underTest = ClassHtml.builder(className).build();

        //when
        final String actual = underTest.getClassNameTextShort();

        //then
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("statusProvider")
    void testIsCollapsedShouldReturnTrueOnlyWhenCalledForSuccess(final StageResultHtml input, final boolean expected) {
        //given
        final ClassHtml underTest = ClassHtml.builder(ClassHtml.class.getName())
                .stats(StatsHtml.builder().worstResult(input).build())
                .build();

        //when
        final boolean actual = underTest.isCollapsed();

        //then
        assertEquals(expected, actual);
    }
}
