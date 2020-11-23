package com.github.nagyesta.abortmission.core.matcher;

import java.util.Comparator;

/**
 * Defines how component matchers should behave.
 */
public interface MissionHealthCheckMatcher extends Comparable<MissionHealthCheckMatcher> {

    /**
     * The name of the matcher in a formatted way.
     *
     * @return name
     */
    String getName();

    /**
     * The type of the matcher we are using.
     *
     * @return type
     */
    MatchCriteria getMatchCriteria();

    /**
     * Matches the component against the matcher and returns the result.
     *
     * @param component The component we want to match.
     * @return true if the component matches this matcher, false otherwise.
     */
    boolean matches(Object component);

    @Override
    default int compareTo(final MissionHealthCheckMatcher o) {
        return Comparator.comparing(MissionHealthCheckMatcher::getMatchCriteria)
                .thenComparing(MissionHealthCheckMatcher::getName)
                .compare(this, o);
    }

    /**
     * The match types we can use.
     */
    enum MatchCriteria {
        /**
         * Logical AND.
         */
        AND,
        /**
         * Logical OR.
         */
        OR,
        /**
         * Logical NOT.
         */
        NOT,
        /**
         * Exact match against a named dependency.
         */
        DEPENDENCY,
        /**
         * Class name matcher.
         */
        CLASS,
        /**
         * Environment variable matching.
         */
        ENVIRONMENT,
        /**
         * System property matching.
         */
        PROPERTY
    }
}
