package com.github.nagyesta.abortmission.core.matcher.impl.builder;

/**
 * Builder interface for {@link com.github.nagyesta.abortmission.core.matcher.impl.ClassMatcher} instances.
 */
@SuppressWarnings("unused")
public interface ClassMatcherBuilder {

    /**
     * Sets the regular expression we want ot use for matching against the fully qualified class name.
     *
     * @param regex the regular expression.
     * @return builder
     */
    FinalMatcherBuilder classNamePattern(String regex);

    /**
     * Will match against any class.
     *
     * @return builder
     */
    default FinalMatcherBuilder anyClass() {
        return this.classNamePattern(".*");
    }
}
