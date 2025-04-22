package com.github.nagyesta.abortmission.core.matcher.impl;

import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;

import java.lang.reflect.Method;

/**
 * {@link RegexMatcher} subclass using class name based matching.
 */
public class ClassMatcher extends RegexMatcher implements MissionHealthCheckMatcher {

    /**
     * Constructor passing the regular expression to the superclass.
     *
     * @param classNamePattern The regular expression.
     */
    ClassMatcher(final String classNamePattern) {
        super(classNamePattern, "Class name regex cannot be null.");
    }

    @Override
    public MatchCriteria getMatchCriteria() {
        return MatchCriteria.CLASS;
    }

    @Override
    protected boolean supports(final Object component) {
        return component instanceof Class<?> || component instanceof Method;
    }

    @Override
    protected String convert(final Object component) {
        if (component instanceof Class<?>) {
            return ((Class<?>) component).getName();
        } else if (component instanceof final Method method) {
            return method.getDeclaringClass().getName();
        } else {
            throw new IllegalArgumentException("Illegal component type provided: " + component);
        }
    }

}
