package com.github.nagyesta.abortmission.core.healthcheck.impl;

import com.github.nagyesta.abortmission.core.healthcheck.StageStatistics;
import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;

import java.util.Objects;

/**
 * Basic implementation of stage statistics collectors.
 */
public abstract class AbstractStageStatisticsCollector implements StageStatistics {

    private final MissionHealthCheckMatcher matcher;

    /**
     * Constructor setting the matcher as well.
     *
     * @param matcher The matcher instance used by the evaluator that will record statistics in this instance.
     */
    protected AbstractStageStatisticsCollector(final MissionHealthCheckMatcher matcher) {
        this.matcher = Objects.requireNonNull(matcher, "Matcher cannot be null.");
    }

    @Override
    public MissionHealthCheckMatcher getMatcher() {
        return matcher;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final AbstractStageStatisticsCollector that)) {
            return false;
        }
        return Objects.equals(matcher, that.matcher);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matcher);
    }
}
