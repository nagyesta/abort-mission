package com.github.nagyesta.abortmission.core.matcher.impl.builder;

/**
 * Builder interface for {@link com.github.nagyesta.abortmission.core.matcher.impl.DependencyMatcher} instances.
 */
public interface DependencyMatcherBuilder {

    /**
     * Sets the name of the dependency which will be used after the name is extracted with
     * {@link com.github.nagyesta.abortmission.core.extractor.impl.MissionDependenciesDependencyNameExtractor}.
     *
     * @param name The exact name we want to match against
     * @return builder
     */
    FinalMatcherBuilder dependency(String name);

    /**
     * Sets the name of the dependency and will allow overriding the
     * {@link com.github.nagyesta.abortmission.core.extractor.impl.MissionDependenciesDependencyNameExtractor}.
     *
     * @param name The exact name we want to match against
     * @return builder
     */
    DependencyNameExtractorMatcherBuilder dependencyWith(String name);

}
