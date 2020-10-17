package com.github.nagyesta.abortmission.core.matcher.impl;

import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;

import java.util.Objects;
import java.util.Optional;

/**
 * {@link RegexMatcher} subclass using environment variable based matching.
 */
public class EnvironmentMatcher extends RegexMatcher implements MissionHealthCheckMatcher {

    private final String environmentVariableName;

    /**
     * Constructor passing the regular expression to the superclass and setting the property name.
     *
     * @param environmentVariableName The property name.
     * @param valuePattern            The regular expression.
     */
    EnvironmentMatcher(final String environmentVariableName, final String valuePattern) {
        super(valuePattern, "Env variable value regex cannot be null.");
        this.environmentVariableName = Objects.requireNonNull(environmentVariableName, "Env var name cannot be null.");
    }

    @Override
    public String getName() {
        return getMatchCriteria() + "_MATCHING(" + environmentVariableName + "='" + getPattern() + "')";
    }

    @Override
    public MatchCriteria getMatchCriteria() {
        return MatchCriteria.ENVIRONMENT;
    }

    @Override
    protected boolean supports(final Object component) {
        return true;
    }

    @Override
    protected String convert(final Object component) {
        return Optional.ofNullable(System.getenv(environmentVariableName)).orElse("");
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EnvironmentMatcher)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final EnvironmentMatcher that = (EnvironmentMatcher) o;
        return environmentVariableName.equals(that.environmentVariableName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), environmentVariableName);
    }
}
