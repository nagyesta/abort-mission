package com.github.nagyesta.abortmission.core.matcher.impl.builder;

/**
 * Builder interface for {@link com.github.nagyesta.abortmission.core.matcher.impl.EnvironmentMatcher}
 * and {@link com.github.nagyesta.abortmission.core.matcher.impl.SystemPropertyMatcher} instances.
 */
public interface PropertyValueMatcherBuilder {

    /**
     * Sets the regular expression we will use for matching against the value.
     *
     * @param regex The regular expression we want to match against.
     * @return builder
     */
    FinalMatcherBuilder valuePattern(String regex);
}
