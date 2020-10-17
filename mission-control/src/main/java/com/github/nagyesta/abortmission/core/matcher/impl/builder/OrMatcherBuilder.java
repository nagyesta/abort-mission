package com.github.nagyesta.abortmission.core.matcher.impl.builder;

import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;

/**
 * Builder interface for {@link com.github.nagyesta.abortmission.core.matcher.impl.OrMatcher} instances.
 */
public interface OrMatcherBuilder extends InitialOrMatcherBuilder {

    /**
     * Adds the final {@link MissionHealthCheckMatcher} to the logical OR chain.
     *
     * @param matcher The final piece of the chain.
     * @return builder
     */
    FinalMatcherBuilder orAtLast(MissionHealthCheckMatcher matcher);

}
