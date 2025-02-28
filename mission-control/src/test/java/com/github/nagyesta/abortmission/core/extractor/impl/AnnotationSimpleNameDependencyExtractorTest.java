package com.github.nagyesta.abortmission.core.extractor.impl;

import com.github.nagyesta.abortmission.core.annotation.LaunchSequence;
import com.github.nagyesta.abortmission.core.outline.MissionOutline;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

@DisplayName("AnnotationSimpleNameDependencyExtractorTest")
@LaunchSequence(MissionOutline.class)
class AnnotationSimpleNameDependencyExtractorTest {

    @Test
    void testExtractValuesFromAnnotationShouldReturnSetOfAnnotationNamesWhenCalledWithAnnotatedClass() {
        //given
        final var underTest = new AnnotationSimpleNameDependencyExtractor();

        //when
        final var actual = underTest.apply(this.getClass());

        //then
        Assertions.assertIterableEquals(List.of("DisplayName", "LaunchSequence"), actual.orElse(Collections.emptySet()));
    }

    @Test
    void testExtractValuesFromAnnotationShouldReturnEmptyWhenCalledWithNotAnnotatedClass() {
        //given
        final var underTest = new AnnotationSimpleNameDependencyExtractor();

        //when
        final var actual = underTest.apply(NotAnnotated.class);

        //then
        Assertions.assertTrue(actual.isEmpty());
    }

    @Test
    void testExtractValuesFromAnnotationShouldReturnEmptyWhenCalledWithNonClassObject() {
        //given
        final var underTest = new AnnotationSimpleNameDependencyExtractor();

        //when
        final var actual = underTest.apply(this);

        //then
        Assertions.assertTrue(actual.isEmpty());
    }

    private static final class NotAnnotated {
    }
}
