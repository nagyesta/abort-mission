package com.github.nagyesta.abortmission.core.extractor.impl;

import com.github.nagyesta.abortmission.core.annotation.MissionDependencies;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MissionDependenciesDependencyNameExtractorTest {

    private static final String A = "a";
    private static final String B = "b";
    private static final String C = "c";
    private static final String D = "d";

    private static Stream<Arguments> methodInputProvider() throws NoSuchMethodException {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, null))
                .add(Arguments.of(MissionDependenciesDependencyNameExtractorTest.class.getDeclaredMethods()[0], null))
                .add(Arguments.of(InputProvider.class.getDeclaredMethod("methodABCD"), new HashSet<>(Arrays.asList(A, B, C, D))))
                .add(Arguments.of(InputProvider.class.getDeclaredMethod("methodABInherited"), new HashSet<>(Arrays.asList(A, B))))
                .add(Arguments.of(InputProvider.class.getDeclaredMethod("methodAB"), new HashSet<>(Arrays.asList(A, B))))
                .add(Arguments.of(InputProvider.class.getDeclaredMethod("methodABD"), new HashSet<>(Arrays.asList(A, B, D))))
                .build();
    }

    private static Stream<Arguments> classInputProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, null))
                .add(Arguments.of(MissionDependenciesDependencyNameExtractorTest.class, null))
                .add(Arguments.of(InputProvider.class, new HashSet<>(Arrays.asList(A, B))))
                .build();
    }

    @ParameterizedTest
    @MethodSource("methodInputProvider")
    void testApplyShouldMergeAllSourcesWhenCalledWithValidMethods(final Method method, final Set<String> expected) {
        //given
        final var underTest = new MissionDependenciesDependencyNameExtractor();

        //when
        final var actual = underTest.apply(method);

        //then
        if (expected == null) {
            assertFalse(actual.isPresent());
        } else {
            assertTrue(actual.isPresent());
            assertIterableEquals(expected, actual.get());
        }
    }

    @ParameterizedTest
    @MethodSource("classInputProvider")
    void testApplyShouldExtractClassDependenciesONlyWhenCalledWithValidClasses(final Class<?> type, final Set<String> expected) {
        //given
        final var underTest = new MissionDependenciesDependencyNameExtractor();

        //when
        final var actual = underTest.apply(type);

        //then
        if (expected == null) {
            assertFalse(actual.isPresent());
        } else {
            assertTrue(actual.isPresent());
            assertIterableEquals(expected, actual.get());
        }
    }

    @MissionDependencies({A, B})
    private static final class InputProvider {

        @MissionDependencies({A, B, C, D})
        public void methodABCD() {

        }

        public void methodABInherited() {

        }

        @MissionDependencies(B)
        public void methodAB() {

        }

        @MissionDependencies(D)
        public void methodABD() {

        }
    }
}
