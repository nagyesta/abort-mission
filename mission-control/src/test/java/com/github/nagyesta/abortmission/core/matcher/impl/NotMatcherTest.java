package com.github.nagyesta.abortmission.core.matcher.impl;

import com.github.nagyesta.abortmission.core.extractor.impl.StringDependencyNameExtractor;
import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotMatcherTest extends AbstractMatcherTest {

    private static final Class<?> CLASS_LITERAL = NotMatcherTest.class;
    private static final long LONG_42 = 42L;
    private static final String MATCHING_DEPENDENCY = "MatchingDependency";
    private static final String UNKNOWN_DEPENDENCY = "UnknownDependency";
    private static final StringDependencyNameExtractor STRING_EXTRACTOR = new StringDependencyNameExtractor();
    private static final MissionHealthCheckMatcher STRING_MATCHER = MissionHealthCheckMatcherBuilder.builder()
            .dependencyWith(MATCHING_DEPENDENCY)
            .extractor(STRING_EXTRACTOR)
            .build();
    private static final MissionHealthCheckMatcher NOT_STRING_MATCHER = MissionHealthCheckMatcherBuilder.builder()
            .not(STRING_MATCHER)
            .build();

    private static Stream<Arguments> inputProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(true, UNKNOWN_DEPENDENCY))
                .add(Arguments.of(false, MATCHING_DEPENDENCY))
                .build();
    }

    private static Stream<Arguments> unsupportedComponentProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(CLASS_LITERAL))
                .add(Arguments.of(LONG_42))
                .build();
    }

    private static Stream<Arguments> equalsAndHashCodeProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(
                        NOT_STRING_MATCHER,
                        STRING_MATCHER,
                        false
                ))
                .add(Arguments.of(
                        NOT_STRING_MATCHER,
                        NOT_STRING_MATCHER,
                        true
                ))
                .add(Arguments.of(
                        NOT_STRING_MATCHER,
                        null,
                        false
                ))
                .add(Arguments.of(
                        MissionHealthCheckMatcherBuilder.builder()
                                .not(STRING_MATCHER).build(),
                        NOT_STRING_MATCHER,
                        true
                ))
                .build();
    }

    @ParameterizedTest
    @MethodSource("inputProvider")
    void testMatchesShouldNegateWrappedMatcherWhenCalledWithValidComponent(final boolean matches, final String dependency) {
        //given
        final var underTest = MissionHealthCheckMatcherBuilder.builder()
                .not(STRING_MATCHER).build();

        //when
        final var actual = underTest.matches(dependency);

        //then
        assertEquals(matches, actual);
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("unsupportedComponentProvider")
    void testMatchesShouldThrowExceptionWhenCalledWithUnsupportedInput(final Object invalidInput) {
        //given
        final var underTest = MissionHealthCheckMatcherBuilder.builder()
                .not(STRING_MATCHER).build();

        //when
        final var actual = underTest.matches(invalidInput);

        //then
        assertTrue(actual);
    }

    @ParameterizedTest
    @MethodSource("equalsAndHashCodeProvider")
    @Override
    protected void testEqualsAndHashCodeShouldBehaveInSyncWhenCalled(final MissionHealthCheckMatcher a,
                                                                     final MissionHealthCheckMatcher b,
                                                                     final boolean expected) {
        super.testEqualsAndHashCodeShouldBehaveInSyncWhenCalled(a, b, expected);
    }
}
