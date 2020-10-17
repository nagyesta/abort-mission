package com.github.nagyesta.abortmission.core.matcher.impl.builder;

/**
 * Builder interface for {@link com.github.nagyesta.abortmission.core.matcher.impl.EnvironmentMatcher} instances.
 */
public interface EnvironmentMatcherBuilder {

    /**
     * Sets the name of the environment variable.
     *
     * @param name The exact name we want to match against
     * @return builder
     */
    PropertyValueMatcherBuilder envVariable(String name);
}
