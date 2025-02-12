package com.github.nagyesta.abortmission.core.extractor.impl;

import com.github.nagyesta.abortmission.core.extractor.DependencyNameExtractor;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class AnnotationSimpleNameDependencyExtractor implements DependencyNameExtractor {

    @Override
    public Optional<Set<String>> apply(final Object o) {
        if (!(o instanceof final Class<?> testClass)) {
            return Optional.empty();
        }

        final Set<String> dependencies = Arrays.stream(testClass.getAnnotations())
                .map(annotation -> annotation.annotationType().getSimpleName()).collect(Collectors.toSet());
        return wrapWithOptional(new TreeSet<>(dependencies));
    }

    private static Optional<Set<String>> wrapWithOptional(final Set<String> dependencies) {
        final Optional<Set<String>> result;
        if (dependencies.isEmpty()) {
            result = Optional.empty();
        } else {
            result = Optional.of(dependencies);
        }
        return result;
    }
}
