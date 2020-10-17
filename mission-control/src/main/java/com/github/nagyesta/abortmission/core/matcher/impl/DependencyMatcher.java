package com.github.nagyesta.abortmission.core.matcher.impl;

import com.github.nagyesta.abortmission.core.extractor.DependencyNameExtractor;
import com.github.nagyesta.abortmission.core.extractor.impl.MissionDependenciesDependencyNameExtractor;
import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * {@link MissionHealthCheckMatcher} implementation using dependency name based matching.
 * A dependency name would be typically based on an annotation/tag name provided by the test framework
 * you are using but we support plain text dependency names as well (although it would pose challenges).
 */
public class DependencyMatcher implements MissionHealthCheckMatcher {

    private final String dependencyName;
    private final DependencyNameExtractor extractor;

    /**
     * Constructor defining the dependency name we want to use for an exact match.
     *
     * @param dependencyName The name of the dependency.
     * @param extractor      The extractor we want to use for dependency name extraction.
     */
    DependencyMatcher(final String dependencyName, final DependencyNameExtractor extractor) {
        Objects.requireNonNull(dependencyName, "Dependency name cannot be null.");
        this.dependencyName = dependencyName;
        this.extractor = Optional.ofNullable(extractor).orElseGet(MissionDependenciesDependencyNameExtractor::new);
    }

    @Override
    public String getName() {
        return dependencyName;
    }

    @Override
    public MatchCriteria getMatchCriteria() {
        return MatchCriteria.DEPENDENCY;
    }

    @Override
    public boolean matches(final Object component) {
        return extractDependencyName(component)
                .map(set -> set.stream().anyMatch(dependencyName::equals))
                .orElse(false);
    }

    private Optional<Set<String>> extractDependencyName(final Object component) {
        return extractor.apply(component);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DependencyMatcher)) {
            return false;
        }
        final DependencyMatcher that = (DependencyMatcher) o;
        return dependencyName.equals(that.dependencyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dependencyName);
    }
}
