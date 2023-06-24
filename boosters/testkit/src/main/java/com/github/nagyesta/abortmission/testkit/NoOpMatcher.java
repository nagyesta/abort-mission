package com.github.nagyesta.abortmission.testkit;

import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;

/**
 * No-Operation matcher just for simple test result definition.
 */
public class NoOpMatcher implements MissionHealthCheckMatcher {

    /**
     * Single instance of {@link NoOpMatcher} for tests.
     */
    public static final NoOpMatcher NOOP = new NoOpMatcher();

    @Override
    public String getName() {
        return null;
    }

    @Override
    public MatchCriteria getMatchCriteria() {
        return null;
    }

    @Override
    public boolean matches(final Object component) {
        return false;
    }

    @Override
    public int compareTo(final MissionHealthCheckMatcher o) {
        if (o instanceof NoOpMatcher) {
            return 0;
        }
        return -1;
    }

}
