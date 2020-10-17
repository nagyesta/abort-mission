package com.github.nagyesta.abortmission.core.matcher.impl;

import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;

import java.util.Set;

/**
 * {@link CombiningOperatorMatcher} subclass representing logical OR operations.
 */
public class OrMatcher extends CombiningOperatorMatcher implements MissionHealthCheckMatcher {

    /**
     * Constructor passing the operands to the superclass.
     *
     * @param operands Operands of this operator.
     */
    OrMatcher(final Set<MissionHealthCheckMatcher> operands) {
        super(operands);
    }

    @Override
    public MatchCriteria getMatchCriteria() {
        return MatchCriteria.OR;
    }

    @Override
    public boolean matches(final Object component) {
        return operands().stream().anyMatch(op -> op.matches(component));
    }

}
