package com.github.nagyesta.abortmission.core.matcher.impl.builder;

/**
 * Builder interface for {@link com.github.nagyesta.abortmission.core.matcher.impl.SystemPropertyMatcher} instances.
 */
public interface SystemPropertyMatcherBuilder {

    /**
     * Sets the name of the system property.
     *
     * @param name The exact name we want to match against
     * @return builder
     */
    PropertyValueMatcherBuilder property(String name);
}
