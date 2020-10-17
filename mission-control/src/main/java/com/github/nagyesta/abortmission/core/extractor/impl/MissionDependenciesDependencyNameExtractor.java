package com.github.nagyesta.abortmission.core.extractor.impl;

import com.github.nagyesta.abortmission.core.annotation.MissionDependencies;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Provides a way to extract dependency names from components with {@link MissionDependencies} annotations.
 */
public class MissionDependenciesDependencyNameExtractor extends AnnotationDependencyNameExtractor<MissionDependencies> {

    @Override
    protected Class<MissionDependencies> getType() {
        return MissionDependencies.class;
    }

    @Override
    protected Collection<String> extractValuesFromAnnotation(final MissionDependencies annotation) {
        return Arrays.stream(annotation.value()).collect(Collectors.toSet());
    }

}
