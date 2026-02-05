package com.github.nagyesta.abortmission.core.matcher.impl;

import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class EnvironmentMatcherTest extends AbstractMatcherTest {

    private static final String NOT_FOUND_ENV_VAR_NAME = "CERTAINLY_NOT-FOUND-Env_Var_name-For__Abort-Mission";
    private static final String NOT_EMPTY = ".+";
    private static final MissionHealthCheckMatcher NOT_FOUND_VARIABLE_NOT_EMPTY_MATCHER = MissionHealthCheckMatcherBuilder.builder()
            .envVariable(NOT_FOUND_ENV_VAR_NAME)
            .valuePattern(NOT_EMPTY)
            .build();
    private static final String PATH = "PATH";
    private static final String NOT_EMPTY_ALTERNATIVE = "..*";
    private static final Integer INT_42 = 42;

    private static Stream<Arguments> validInputProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(NOT_FOUND_ENV_VAR_NAME, NOT_EMPTY, INT_42, false))
                .add(Arguments.of(PATH, NOT_EMPTY, INT_42, true))
                .add(Arguments.of(NOT_FOUND_ENV_VAR_NAME, NOT_EMPTY, NOT_EMPTY, false))
                .add(Arguments.of(PATH, NOT_EMPTY, NOT_EMPTY, true))
                .build();
    }

    private static Stream<Arguments> equalsAndHashCodeProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(
                        MissionHealthCheckMatcherBuilder.builder()
                                .envVariable(NOT_FOUND_ENV_VAR_NAME)
                                .valuePattern(NOT_EMPTY)
                                .build(),
                        NOT_FOUND_VARIABLE_NOT_EMPTY_MATCHER,
                        true
                ))
                .add(Arguments.of(
                        NOT_FOUND_VARIABLE_NOT_EMPTY_MATCHER,
                        NOT_FOUND_VARIABLE_NOT_EMPTY_MATCHER,
                        true
                ))
                .add(Arguments.of(
                        MissionHealthCheckMatcherBuilder.builder()
                                .envVariable(NOT_FOUND_ENV_VAR_NAME)
                                .valuePattern(NOT_EMPTY)
                                .build(),
                        null,
                        false
                ))
                .add(Arguments.of(
                        MissionHealthCheckMatcherBuilder.builder()
                                .envVariable(NOT_FOUND_ENV_VAR_NAME)
                                .valuePattern(NOT_EMPTY_ALTERNATIVE)
                                .build(),
                        MissionHealthCheckMatcherBuilder.builder()
                                .envVariable(NOT_FOUND_ENV_VAR_NAME)
                                .valuePattern(NOT_EMPTY)
                                .build(),
                        false
                ))
                .build();
    }

    @ParameterizedTest
    @MethodSource("validInputProvider")
    void testMatchesShouldWorkRegardlessOfTheInput(
            final String envName,
            final String regex,
            final Object component,
            final boolean expected) {
        //given
        final var underTest = MissionHealthCheckMatcherBuilder.builder()
                .envVariable(envName)
                .valuePattern(regex)
                .build();
        assertInstanceOf(EnvironmentMatcher.class, underTest);

        //when
        final var actual = underTest.matches(component);

        //then
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("equalsAndHashCodeProvider")
    @Override
    protected void testEqualsAndHashCodeShouldBehaveInSyncWhenCalled(
            final MissionHealthCheckMatcher a,
            final MissionHealthCheckMatcher b,
            final boolean expected) {
        super.testEqualsAndHashCodeShouldBehaveInSyncWhenCalled(a, b, expected);
    }

}
