package com.github.nagyesta.abortmission.core.matcher.impl.builder;

import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;

/**
 * Builder interface for {@link com.github.nagyesta.abortmission.core.matcher.impl.AndMatcher} instances.
 */
public interface InitialAndMatcherBuilder {

    /**
     * Adds {@link MissionHealthCheckMatcher} to the logical AND chain.
     *
     * @param matcher A piece of the chain.
     * @return builder
     */
    AndMatcherBuilder and(MissionHealthCheckMatcher matcher);
}
