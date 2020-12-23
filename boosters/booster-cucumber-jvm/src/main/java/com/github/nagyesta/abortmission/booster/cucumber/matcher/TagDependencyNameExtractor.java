package com.github.nagyesta.abortmission.booster.cucumber.matcher;

import com.github.nagyesta.abortmission.core.extractor.DependencyNameExtractor;
import io.cucumber.java.Scenario;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Simple implementation extracting Scenario tag names as dependencies.
 */
public class TagDependencyNameExtractor implements DependencyNameExtractor {

    @Override
    public Optional<Set<String>> apply(final Object component) {
        return Optional.ofNullable(component)
                .filter(c -> c instanceof Scenario)
                .map(Scenario.class::cast)
                .map(Scenario::getSourceTagNames)
                .map(Collection::stream)
                .map(s -> s.filter(tag -> !tag.startsWith("AbortMission_"))
                        .map(t -> t.replaceFirst("^@", ""))
                        .collect(Collectors.toSet()));
    }
}
