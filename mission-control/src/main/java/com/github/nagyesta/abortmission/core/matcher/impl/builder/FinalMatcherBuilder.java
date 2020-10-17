package com.github.nagyesta.abortmission.core.matcher.impl.builder;

import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;

/**
 * Builder interface for {@link com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher} instances.
 */
public interface FinalMatcherBuilder {

    /**
     * Builds the matcher.
     * <p>
     * NOTE: Builder cannot be reused after this point.
     *
     * @return the new matcher instance.
     */
    MissionHealthCheckMatcher build();
}
