package com.github.nagyesta.abortmission.core.extractor.impl;

import com.github.nagyesta.abortmission.core.extractor.DependencyNameExtractor;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * Simple implementation essentially only casting the component to a {@link String} and wrapping with a {@link Set}.
 */
public class StringDependencyNameExtractor implements DependencyNameExtractor {

    @Override
    public Optional<Set<String>> apply(final Object component) {
        return Optional.ofNullable(component)
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .filter(s -> !s.isEmpty())
                .map(Collections::singleton);
    }
}
