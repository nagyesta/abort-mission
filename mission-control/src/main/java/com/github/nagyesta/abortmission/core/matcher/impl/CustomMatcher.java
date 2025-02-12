package com.github.nagyesta.abortmission.core.matcher.impl;

import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;

import java.util.Objects;

/**
 * {@link MissionHealthCheckMatcher} implementation allowing custom matching.
 */
public abstract class CustomMatcher implements MissionHealthCheckMatcher {

    @Override
    public abstract String getName();

    @Override
    public MatchCriteria getMatchCriteria() {
        return MatchCriteria.CUSTOM;
    }

    @Override
    public abstract boolean matches(Object component);

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final CustomMatcher that)) {
            return false;
        }
        return getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
