package com.github.nagyesta.abortmission.core.matcher.impl;

import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Superclass for {@link MissionHealthCheckMatcher} instances which need to match using a regular expression.
 */
public abstract class RegexMatcher extends BaseMatcher implements MissionHealthCheckMatcher {

    private final String pattern;
    private final Pattern compiledPattern;

    /**
     * Constructor validating and setting the regular expression.
     *
     * @param pattern The regular expression.
     * @param message The message we want to use in case the expression is null.
     */
    RegexMatcher(final String pattern, final String message) {
        this.pattern = Objects.requireNonNull(pattern, message);
        this.compiledPattern = Pattern.compile(pattern);
    }

    /**
     * Returns the regular expression held by this instance.
     *
     * @return The regular expression
     */
    public final String getPattern() {
        return pattern;
    }

    @Override
    public String getName() {
        return getMatchCriteria().name() + "_MATCHING('" + pattern + "')";
    }

    @Override
    public final boolean matches(final Object component) {
        if (!supports(component)) {
            return false;
        }
        return compiledPattern.matcher(convert(component)).matches();
    }

    protected abstract boolean supports(Object component);

    protected abstract String convert(Object component);

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(this.getClass().isInstance(o))) {
            return false;
        }
        final RegexMatcher that = this.getClass().cast(o);
        return this.getMatchCriteria() == that.getMatchCriteria() && pattern.equals(that.getPattern());
    }

    @Override
    public int hashCode() {
        return Objects.hash(pattern, getMatchCriteria());
    }
}
