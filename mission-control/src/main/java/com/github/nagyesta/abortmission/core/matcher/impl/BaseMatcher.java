package com.github.nagyesta.abortmission.core.matcher.impl;

import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;

/**
 * Common superclass of all {@link MissionHealthCheckMatcher} implementations.
 */
public abstract class BaseMatcher implements MissionHealthCheckMatcher {

    @Override
    public String toString() {
        return this.getName();
    }
}
