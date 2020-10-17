package com.github.nagyesta.abortmission.core.matcher.impl.builder;

import com.github.nagyesta.abortmission.core.extractor.DependencyNameExtractor;

/**
 * Builder interface for {@link com.github.nagyesta.abortmission.core.matcher.impl.DependencyMatcher} instances.
 */
public interface DependencyNameExtractorMatcherBuilder {

    /**
     * Defines which {@link DependencyNameExtractor} will be used to find out the name of the dependency.
     *
     * @param extractor The extractor we want to use.
     * @return builder
     */
    FinalMatcherBuilder extractor(DependencyNameExtractor extractor);

}
