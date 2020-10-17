package com.github.nagyesta.abortmission.core.matcher.impl.builder;

import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;

/**
 * Builder interface for {@link com.github.nagyesta.abortmission.core.matcher.impl.NotMatcher} instances.
 */
public interface NotMatcherBuilder {

    /**
     * Sets the {@link MissionHealthCheckMatcher} we will negate by this matcher.
     *
     * @param matcher The matcher we will negate.
     * @return builder
     */
    FinalMatcherBuilder not(MissionHealthCheckMatcher matcher);

}
