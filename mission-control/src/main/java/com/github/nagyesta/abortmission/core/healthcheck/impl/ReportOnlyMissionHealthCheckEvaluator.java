package com.github.nagyesta.abortmission.core.healthcheck.impl;

import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;

import java.util.Objects;

/**
 * {@link com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator} implementation intended to only collect
 * telemetry and never abort.
 */
@SuppressWarnings("checkstyle:FinalClass")
public class ReportOnlyMissionHealthCheckEvaluator extends AbstractMissionHealthCheckEvaluator {

    private ReportOnlyMissionHealthCheckEvaluator(final Builder builder) {
        super(Objects.requireNonNull(builder, "Builder cannot be null.").matcher, new MissionStatisticsCollector());
    }

    public static Builder builder(final MissionHealthCheckMatcher matcher) {
        return new Builder(matcher);
    }

    @Override
    public int getBurnInTestCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    protected boolean shouldAbortInternal() {
        return false;
    }

    @Override
    protected boolean shouldAbortCountdownInternal() {
        return false;
    }

    @SuppressWarnings({"checkstyle:HiddenField", "checkstyle:DesignForExtension"})
    public static final class Builder {
        private final MissionHealthCheckMatcher matcher;

        private Builder(final MissionHealthCheckMatcher matcher) {
            this.matcher = Objects.requireNonNull(matcher, "Matcher cannot be null.");
        }

        public ReportOnlyMissionHealthCheckEvaluator build() {
            return new ReportOnlyMissionHealthCheckEvaluator(this);
        }
    }
}
