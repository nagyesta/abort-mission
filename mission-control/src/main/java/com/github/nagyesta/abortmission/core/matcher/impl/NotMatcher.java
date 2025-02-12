package com.github.nagyesta.abortmission.core.matcher.impl;

import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;

import java.util.Objects;

/**
 * {@link MissionHealthCheckMatcher} implementation representing a logical NOT operator.
 */
public class NotMatcher extends BaseMatcher implements MissionHealthCheckMatcher {

    private final MissionHealthCheckMatcher operand;

    /**
     * Constructor setting the operand wrapped by this NOT operator.
     *
     * @param operand The matcher we want to negate.
     */
    NotMatcher(final MissionHealthCheckMatcher operand) {
        this.operand = Objects.requireNonNull(operand, "Operand cannot be null.");
    }

    @Override
    public String getName() {
        return getMatchCriteria().name() + "(" + operand.getName() + ")";
    }

    @Override
    public MatchCriteria getMatchCriteria() {
        return MatchCriteria.NOT;
    }

    @Override
    public boolean matches(final Object component) {
        return !operand.matches(component);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final NotMatcher that)) {
            return false;
        }
        return operand.equals(that.operand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operand);
    }
}
