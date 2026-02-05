package com.github.nagyesta.abortmission.core.annotation;

import com.github.nagyesta.abortmission.core.AbortMissionCommandOps;
import com.github.nagyesta.abortmission.core.outline.MissionOutline;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.opentest4j.TestAbortedException;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

class AnnotationContextEvaluatorTest {

    private static final String CONTEXT_NAME = "fakeContextName";
    private static final String EMPTY_CONTEXT_NAME = " ";
    private static final String METHOD_NAME = "method";

    private static Stream<Arguments> contextNameClassProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(TestClass.class, Optional.empty()))
                .add(Arguments.of(EmptyTestClass.class, Optional.empty()))
                .add(Arguments.of(AnnotatedTestClass.class, Optional.of(CONTEXT_NAME)))
                .build();
    }

    private static Stream<Arguments> contextNameMethodProvider() throws NoSuchMethodException {
        return Stream.<Arguments>builder()
                .add(Arguments.of(TestClass.class.getDeclaredMethod(METHOD_NAME), Optional.empty()))
                .add(Arguments.of(EmptyTestClass.class.getDeclaredMethod(METHOD_NAME), Optional.empty()))
                .add(Arguments.of(AnnotatedTestClass.class.getDeclaredMethod(METHOD_NAME), Optional.of(CONTEXT_NAME)))
                .build();
    }

    private static Stream<Arguments> abortSuppressedClassProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(TestClass.class, false))
                .add(Arguments.of(EmptyTestClass.class, false))
                .add(Arguments.of(AnnotatedTestClass.class, true))
                .build();
    }

    private static Stream<Arguments> abortSuppressedMethodProvider() throws NoSuchMethodException {
        return Stream.<Arguments>builder()
                .add(Arguments.of(TestClass.class.getDeclaredMethod(METHOD_NAME), true))
                .add(Arguments.of(EmptyTestClass.class.getDeclaredMethod(METHOD_NAME), false))
                .add(Arguments.of(AnnotatedTestClass.class.getDeclaredMethod(METHOD_NAME), true))
                .build();
    }

    private static Stream<Arguments> failureSuppressedClassProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(TestClass.class, Collections.emptySet()))
                .add(Arguments.of(EmptyTestClass.class, Collections.emptySet()))
                .add(Arguments.of(AnnotatedTestClass.class, Collections.singleton(NullPointerException.class)))
                .build();
    }

    private static Stream<Arguments> failureSuppressedMethodProvider() throws NoSuchMethodException {
        return Stream.<Arguments>builder()
                .add(Arguments.of(TestClass.class.getDeclaredMethod(METHOD_NAME),
                        Collections.emptySet()))
                .add(Arguments.of(EmptyTestClass.class.getDeclaredMethod(METHOD_NAME),
                        Collections.singleton(NullPointerException.class)))
                .add(Arguments.of(AnnotatedTestClass.class.getDeclaredMethod(METHOD_NAME),
                        Arrays.asList(NullPointerException.class, IllegalStateException.class)))
                .build();
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @ParameterizedTest
    @MethodSource("contextNameClassProvider")
    void testFindContextNameShouldWorkWithGenericAnnotationsWhenCalledWithClass(
            final Class<?> component,
            final Optional<String> expected) {
        //given
        final var underTest = AnnotationContextEvaluator.shared();

        //when
        final var actual = underTest.findContextName(component, DisplayName.class, DisplayName::value);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @ParameterizedTest
    @MethodSource("contextNameMethodProvider")
    void testFindContextNameShouldWorkWithGenericAnnotationsWhenCalledWithMethod(
            final Method component,
            final Optional<String> expected) {
        //given
        final var underTest = AnnotationContextEvaluator.shared();

        //when
        final var actual = underTest.findContextName(component, DisplayName.class, DisplayName::value);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("abortSuppressedClassProvider")
    void testIsAbortSuppressedShouldWorkWhenCalledWithClass(
            final Class<?> component,
            final boolean expected) {
        //given
        final var underTest = AnnotationContextEvaluator.shared();

        //when
        final var actual = underTest.isAbortSuppressed(component);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("abortSuppressedMethodProvider")
    void testIsAbortSuppressedShouldWorkWhenCalledWithMethod(
            final Method component,
            final boolean expected) {
        //given
        final var underTest = AnnotationContextEvaluator.shared();

        //when
        final var actual = underTest.isAbortSuppressed(component);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("failureSuppressedClassProvider")
    void testFindSuppressedExceptionsShouldWorkWhenCalledWithClass(
            final Class<?> component,
            final Collection<? extends Exception> expected) {
        //given
        final var underTest = AnnotationContextEvaluator.shared();

        //when
        final var actual = underTest.findSuppressedExceptions(component);

        //then
        Assertions.assertIterableEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("failureSuppressedMethodProvider")
    void testFindSuppressedExceptionsShouldWorkWhenCalledWithMethod(
            final Method component,
            final Collection<? extends Exception> expected) {
        //given
        final var underTest = AnnotationContextEvaluator.shared();

        //when
        final var actual = underTest.findSuppressedExceptions(component);

        //then
        Assertions.assertTrue(expected.containsAll(actual));
        Assertions.assertTrue(actual.containsAll(expected));
    }

    @SuppressWarnings("java:S2699")
    @Test
    void testFindAndApplyLaunchPlanDefinitionShouldFailSilentlyWhenCalledWithoutMatchingConfig() {
        //given
        final var underTest = AnnotationContextEvaluator.shared();

        //when
        underTest.findAndApplyLaunchPlanDefinition(TestClass.class);

        //then no exception
    }


    @Test
    void testFindAndApplyLaunchPlanDefinitionShouldFailWhenMissionOutlineThrowsException() {
        //given
        final var underTest = AnnotationContextEvaluator.shared();

        //when
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> underTest.findAndApplyLaunchPlanDefinition(AnnotatedTestClass.class));

        //then exception
    }

    @SuppressWarnings("checkstyle:VisibilityModifier")
    static class MissionOutlineDef extends MissionOutline {

        protected MissionOutlineDef() {
        }

        @Override
        protected Map<String, Consumer<AbortMissionCommandOps>> defineOutline() {
            throw new TestAbortedException("abort");
        }
    }

    private static final class TestClass {

        @SuppressWarnings("java:S1186") //empty implementation (we just need the method to be there
        @SuppressAbortDecisions
        private void method() {

        }
    }

    @DisplayName(EMPTY_CONTEXT_NAME)
    private static final class EmptyTestClass {

        @SuppressWarnings("java:S1186") //empty implementation (we just need the method to be there
        @SuppressLaunchFailureReporting(forExceptions = NullPointerException.class)
        private void method() {

        }
    }

    @DisplayName(CONTEXT_NAME)
    @SuppressLaunchFailureReporting(forExceptions = NullPointerException.class)
    @SuppressAbortDecisions
    @LaunchSequence(MissionOutlineDef.class)
    private static final class AnnotatedTestClass {

        @SuppressWarnings("java:S1186") //empty implementation (we just need the method to be there
        @SuppressLaunchFailureReporting(forExceptions = IllegalStateException.class)
        private void method() {

        }
    }
}
