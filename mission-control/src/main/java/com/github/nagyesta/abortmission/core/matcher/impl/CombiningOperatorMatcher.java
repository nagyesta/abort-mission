package com.github.nagyesta.abortmission.core.matcher.impl;

import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Superclass for {@link MissionHealthCheckMatcher} instances which need to handle matching based on an
 * operator that can combine the results of operand matchers. For example logical AND, logical OR chains etc.
 */
public abstract class CombiningOperatorMatcher extends BaseMatcher implements MissionHealthCheckMatcher {

    private final Set<MissionHealthCheckMatcher> operands;

    /**
     * Constructor validating and setting operands.
     *
     * @param operands The operands of this matcher.
     */
    CombiningOperatorMatcher(final Set<MissionHealthCheckMatcher> operands) {
        if (operands == null || operands.isEmpty()) {
            throw new IllegalArgumentException("Operand Set cannot be empty or null.");
        } else if (operands.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Operand Set cannot contain null.");
        }
        this.operands = Collections.unmodifiableSet(new TreeSet<>(operands));
    }

    /**
     * Returns the operands of this instance in an unmodifiable {@link Set}.
     *
     * @return the operands
     */
    protected final Set<MissionHealthCheckMatcher> operands() {
        return operands;
    }

    @Override
    public String getName() {
        return operands.stream().map(MissionHealthCheckMatcher::getName)
                .collect(Collectors.joining(", ", getMatchCriteria().name() + "(", ")"));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CombiningOperatorMatcher)) {
            return false;
        }
        final CombiningOperatorMatcher that = (CombiningOperatorMatcher) o;
        return getMatchCriteria() == ((CombiningOperatorMatcher) o).getMatchCriteria() && operands.equals(that.operands);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMatchCriteria(), operands);
    }

}
