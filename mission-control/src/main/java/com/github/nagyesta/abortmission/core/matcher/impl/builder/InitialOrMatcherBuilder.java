package com.github.nagyesta.abortmission.core.matcher.impl.builder;

import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;

/**
 * Builder interface for {@link com.github.nagyesta.abortmission.core.matcher.impl.OrMatcher} instances.
 */
public interface InitialOrMatcherBuilder {

    /**
     * Adds {@link MissionHealthCheckMatcher} to the logical OR chain.
     *
     * @param matcher A piece of the chain.
     * @return builder
     */
    OrMatcherBuilder or(MissionHealthCheckMatcher matcher);

}
