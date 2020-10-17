package com.github.nagyesta.abortmission.core.extractor.impl;

import com.github.nagyesta.abortmission.core.extractor.DependencyNameExtractor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Provides a way to extract dependency names from components with annotations.
 *
 * @param <A> The supported annotation type.
 */
public abstract class AnnotationDependencyNameExtractor<A extends Annotation> implements DependencyNameExtractor {

    @Override
    public Optional<Set<String>> apply(final Object o) {
        Set<String> tags = new TreeSet<>();
        if (o instanceof Method) {
            final Method m = (Method) o;
            accumulateTags(tags, m.getAnnotationsByType(getType()));
            accumulateTags(tags, m.getDeclaringClass().getAnnotationsByType(getType()));
        } else if (o instanceof Class) {
            accumulateTags(tags, ((Class<?>) o).getAnnotationsByType(getType()));
        }

        if (tags.isEmpty()) {
            tags = null;
        }
        return Optional.ofNullable(tags);
    }

    /**
     * Returns the type of the annotation.
     *
     * @return annotation
     */
    protected abstract Class<A> getType();

    /**
     * Returns the set of dependency names extracted from a single annotation.
     *
     * @param annotation The annotation we want to process.
     * @return The extracted names.
     */
    protected abstract Collection<String> extractValuesFromAnnotation(A annotation);

    private void accumulateTags(final Set<String> tags, final A[] annotations) {
        if (annotations != null) {
            Arrays.stream(annotations)
                    .map(this::extractValuesFromAnnotation)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .filter(Objects::nonNull)
                    .forEach(tags::add);
        }
    }
}
