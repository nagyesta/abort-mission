package com.github.nagyesta.abortmission.core.matcher.impl.builder;

import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;

/**
 * Builder interface for {@link com.github.nagyesta.abortmission.core.matcher.impl.AndMatcher} instances.
 */
public interface AndMatcherBuilder extends InitialAndMatcherBuilder {

    /**
     * Adds the final {@link MissionHealthCheckMatcher} to the logical AND chain.
     *
     * @param matcher The final piece of the chain.
     * @return builder
     */
    FinalMatcherBuilder andAtLast(MissionHealthCheckMatcher matcher);
}
