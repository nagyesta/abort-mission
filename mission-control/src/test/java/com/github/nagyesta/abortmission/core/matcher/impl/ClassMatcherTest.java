package com.github.nagyesta.abortmission.core.matcher.impl;

import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ClassMatcherTest extends AbstractMatcherTest {

    private static final String STRING = "string";
    private static final long LONG_42 = 42L;
    private static final MissionHealthCheckMatcher ANY_CLASS_MATCHER = MissionHealthCheckMatcherBuilder.builder()
            .anyClass()
            .build();
    private static final String ANY = ".*";

    private static Stream<Arguments> inputProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(false, ClassMatcherTest.class.getSimpleName()))
                .add(Arguments.of(true, ANY + ClassMatcherTest.class.getSimpleName()))
                .add(Arguments.of(true, ClassMatcherTest.class.getName().replaceAll("\\.", "\\\\.")))
                .build();
    }

    private static Stream<Arguments> unsupportedComponentProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(STRING))
                .add(Arguments.of(LONG_42))
                .build();
    }

    private static Stream<Arguments> equalsAndHashCodeProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(
                        ANY_CLASS_MATCHER,
                        MissionHealthCheckMatcherBuilder.builder()
                                .classNamePattern(ClassMatcherTest.class.getSimpleName())
                                .build(),
                        false
                ))
                .add(Arguments.of(
                        ANY_CLASS_MATCHER,
                        ANY_CLASS_MATCHER,
                        true
                ))
                .add(Arguments.of(
                        ANY_CLASS_MATCHER,
                        null,
                        false
                ))
                .add(Arguments.of(
                        MissionHealthCheckMatcherBuilder.builder()
                                .anyClass()
                                .build(),
                        ANY_CLASS_MATCHER,
                        true
                ))
                .add(Arguments.of(
                        MissionHealthCheckMatcherBuilder.builder()
                                .anyClass()
                                .build(),
                        MissionHealthCheckMatcherBuilder.builder()
                                .property(STRING)
                                .valuePattern(ANY)
                                .build(),
                        false
                ))
                .build();
    }

    @ParameterizedTest
    @MethodSource("inputProvider")
    void testMatchesShouldMatchFullyQualifiedClassNameToRegexpWhenCalledWithClass(final boolean matches, final String pattern) {
        //given
        final var underTest = MissionHealthCheckMatcherBuilder.builder().classNamePattern(pattern).build();

        //when
        final var actual = underTest.matches(this.getClass());

        //then
        assertEquals(matches, actual);
    }

    @ParameterizedTest
    @MethodSource("inputProvider")
    void testMatchesShouldMatchFullyQualifiedClassNameToRegexpWhenCalledWithMethod(final boolean matches, final String pattern) {
        //given
        final var underTest = MissionHealthCheckMatcherBuilder.builder().classNamePattern(pattern).build();

        //when
        final var actual = underTest.matches(this.getClass().getDeclaredMethods()[0]);

        //then
        assertEquals(matches, actual);
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("unsupportedComponentProvider")
    void testMatchesShouldThrowExceptionWhenCalledWithUnsupportedInput(final Object invalidInput) {
        //given
        final var underTest = MissionHealthCheckMatcherBuilder.builder().classNamePattern(".*").build();

        //when
        final var actual = underTest.matches(invalidInput);

        //then
        assertFalse(actual);
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
