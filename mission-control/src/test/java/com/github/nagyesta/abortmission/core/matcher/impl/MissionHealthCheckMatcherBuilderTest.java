package com.github.nagyesta.abortmission.core.matcher.impl;

import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;
import org.junit.jupiter.api.Test;

import static com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher.MatchCriteria.*;
import static org.junit.jupiter.api.Assertions.*;

class MissionHealthCheckMatcherBuilderTest {

    private static final String REGEX = "regex_value";
    public static final MissionHealthCheckMatcher A_CLASS_MATCHER = MissionHealthCheckMatcherBuilder.builder()
            .classNamePattern(REGEX)
            .build();
    private static final String NAME = "name_value";
    public static final MissionHealthCheckMatcher A_DEPENDENCY_MATCHER = MissionHealthCheckMatcherBuilder.builder()
            .dependency(NAME)
            .build();

    @Test
    void testBuilderShouldThrowExceptionWhenBuildCalledWithoutInitializationFirst() {
        //given
        //when
        assertThrows(NullPointerException.class,
                () -> ((MissionHealthCheckMatcherBuilder) MissionHealthCheckMatcherBuilder.builder()).build(),
                "Builder not initialized.");

        //then + exception
    }

    @Test
    void testBuilderShouldAllowBuildingNotMatcherWhenCalledWithValidInput() {
        //given
        //when
        final MissionHealthCheckMatcher actual = MissionHealthCheckMatcherBuilder.builder()
                .not(A_CLASS_MATCHER)
                .build();

        //then
        assertEquals(NOT, actual.getMatchCriteria());
        assertTrue(actual.getName().contains(NOT.name()));
    }

    @Test
    void testBuilderShouldAllowBuildingOrMatcherWhenCalledWithValidInput() {
        //given
        //when
        final MissionHealthCheckMatcher actual = MissionHealthCheckMatcherBuilder.builder()
                .or(A_CLASS_MATCHER)
                .orAtLast(A_DEPENDENCY_MATCHER).build();

        //then
        assertEquals(OR, actual.getMatchCriteria());
        assertTrue(actual.getName().contains(OR.name()));
        assertTrue(actual.getName().contains(REGEX));
        assertTrue(actual.getName().contains(NAME));
    }

    @Test
    void testBuilderShouldAllowBuildingAndMatcherWhenCalledWithValidInput() {
        //given
        //when
        final MissionHealthCheckMatcher actual = MissionHealthCheckMatcherBuilder.builder()
                .and(A_CLASS_MATCHER)
                .andAtLast(A_DEPENDENCY_MATCHER).build();

        //then
        assertEquals(AND, actual.getMatchCriteria());
        assertTrue(actual.getName().contains(AND.name()));
        assertTrue(actual.getName().contains(REGEX));
        assertTrue(actual.getName().contains(NAME));
    }

    @Test
    void testBuilderShouldAllowBuildingClassMatcherWhenCalledWithValidInput() {
        //given
        //when
        final MissionHealthCheckMatcher actual = MissionHealthCheckMatcherBuilder.builder()
                .classNamePattern(REGEX)
                .build();

        //then
        assertEquals(CLASS, actual.getMatchCriteria());
        assertTrue(actual.getName().contains(REGEX));
    }

    @Test
    void testBuilderShouldAllowBuildingDependencyMatcherWhenCalledWithValidInput() {
        //given
        //when
        final MissionHealthCheckMatcher actual = MissionHealthCheckMatcherBuilder.builder()
                .dependency(NAME)
                .build();

        //then
        assertEquals(DEPENDENCY, actual.getMatchCriteria());
        assertTrue(actual.getName().contains(NAME));
    }

    @Test
    void testBuilderShouldAllowBuildingSystemPropertyMatcherWhenCalledWithValidInput() {
        //given
        //when
        final MissionHealthCheckMatcher actual = MissionHealthCheckMatcherBuilder.builder()
                .property(NAME)
                .valuePattern(REGEX)
                .build();

        //then
        assertEquals(PROPERTY, actual.getMatchCriteria());
        assertTrue(actual.getName().contains(NAME));
        assertTrue(actual.getName().contains(REGEX));
    }

    @Test
    void testBuilderShouldAllowBuildingEnvVariableMatcherWhenCalledWithValidInput() {
        //given
        //when
        final MissionHealthCheckMatcher actual = MissionHealthCheckMatcherBuilder.builder()
                .envVariable(NAME)
                .valuePattern(REGEX)
                .build();

        //then
        assertEquals(ENVIRONMENT, actual.getMatchCriteria());
        assertTrue(actual.getName().contains(NAME));
        assertTrue(actual.getName().contains(REGEX));
    }
}
