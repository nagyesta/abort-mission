package com.github.nagyesta.abortmission.core.matcher.impl;

import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;

import java.util.Objects;

/**
 * {@link RegexMatcher} subclass using system property based matching.
 */
public class SystemPropertyMatcher extends RegexMatcher implements MissionHealthCheckMatcher {

    private final String systemPropertyName;

    /**
     * Constructor passing the regular expression to the superclass and setting the property name.
     *
     * @param systemPropertyName The property name.
     * @param valuePattern       The regular expression.
     */
    SystemPropertyMatcher(final String systemPropertyName, final String valuePattern) {
        super(valuePattern, "System property value regex cannot be null.");
        this.systemPropertyName = Objects.requireNonNull(systemPropertyName, "System property name cannot be null.");
    }

    @Override
    public String getName() {
        return getMatchCriteria() + "_MATCHING(" + systemPropertyName + "='" + getPattern() + "')";
    }

    @Override
    public MatchCriteria getMatchCriteria() {
        return MatchCriteria.PROPERTY;
    }

    @Override
    protected boolean supports(final Object component) {
        return true;
    }

    @Override
    protected String convert(final Object component) {
        return System.getProperty(systemPropertyName, "");
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final SystemPropertyMatcher that)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return systemPropertyName.equals(that.systemPropertyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), systemPropertyName);
    }
}
