package com.github.nagyesta.abortmission.core.matcher.impl;

import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

abstract class BaseCombiningMatcherTest extends AbstractMatcherTest {

    protected static final MissionHealthCheckMatcher ANY_CLASS_MATCHER = MissionHealthCheckMatcherBuilder.builder()
            .anyClass()
            .build();
    protected static final MissionHealthCheckMatcher TEST_CLASS_MATCHER =
            MissionHealthCheckMatcherBuilder.builder().classNamePattern(".*Test").build();

    protected static Stream<Arguments> inputProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(true, Arrays.asList(".*\\.And.*Test", ".*\\..*Test", "^.*\\.AndMatcherTest$")))
                .add(Arguments.of(false, Arrays.asList(".*\\.And.*Tests", ".*\\.A.*Test", "^.*\\.AndMatcherTest$")))
                .add(Arguments.of(false, Arrays.asList(".*And.*Tes", ".*\\.A.*Test")))
                .add(Arguments.of(true, Arrays.asList(".*\\..*Test", ".*\\..*Test")))
                .build();
    }

    protected static Stream<Arguments> nullItemProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(Collections.singleton(null)))
                .build();
    }
}
