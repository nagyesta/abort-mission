package com.github.nagyesta.abortmission.core.matcher.impl;

import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;

import java.util.Set;

/**
 * {@link CombiningOperatorMatcher} subclass representing logical AND operations.
 */
public class AndMatcher extends CombiningOperatorMatcher implements MissionHealthCheckMatcher {

    /**
     * Constructor passing the operands to the superclass.
     *
     * @param operands Operands of this operator.
     */
    AndMatcher(final Set<MissionHealthCheckMatcher> operands) {
        super(operands);
    }

    @Override
    public MatchCriteria getMatchCriteria() {
        return MatchCriteria.AND;
    }

    @Override
    public boolean matches(final Object component) {
        return operands().stream().allMatch(op -> op.matches(component));
    }
}
