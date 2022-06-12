package com.github.nagyesta.abortmission.core.healthcheck.impl;

import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;

import java.util.Objects;

/**
 * {@link com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator} implementation intended to always abort.
 */
@SuppressWarnings("checkstyle:FinalClass")
public class AlwaysAbortingMissionHealthCheckEvaluator extends AbstractMissionHealthCheckEvaluator {

    private AlwaysAbortingMissionHealthCheckEvaluator(final Builder builder) {
        super(Objects.requireNonNull(builder, "Builder cannot be null.").matcher,
                builder.statisticsCollector, builder.overrideKeyword);
    }

    public static Builder builder(final MissionHealthCheckMatcher matcher,
                                  final MissionStatisticsCollector statisticsCollector) {
        return new Builder(matcher, statisticsCollector);
    }

    @Override
    public int getBurnInTestCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    protected boolean shouldAbortInternal() {
        return true;
    }

    @Override
    protected boolean shouldAbortCountdownInternal() {
        return true;
    }

    @SuppressWarnings({"checkstyle:HiddenField", "checkstyle:DesignForExtension"})
    public static final class Builder {
        private final MissionHealthCheckMatcher matcher;
        private final MissionStatisticsCollector statisticsCollector;
        private String overrideKeyword;

        private Builder(final MissionHealthCheckMatcher matcher,
                        final MissionStatisticsCollector statisticsCollector) {
            this.matcher = Objects.requireNonNull(matcher, "Matcher cannot be null.");
            this.statisticsCollector = Objects.requireNonNull(statisticsCollector, "Statistic collector cannot be null.");
        }

        public Builder overrideKeyword(final String overrideKeyword) {
            if (overrideKeyword == null || overrideKeyword.isBlank()) {
                throw new IllegalArgumentException("Override keyword must br non-blank.");
            } else if (!overrideKeyword.matches("[\\da-zA-Z\\-]+")) {
                throw new IllegalArgumentException("Override keyword must contain only alpha-numeric characters and dash (a-zA-Z0-9\\-).");
            }
            this.overrideKeyword = overrideKeyword;
            return this;
        }

        public AlwaysAbortingMissionHealthCheckEvaluator build() {
            return new AlwaysAbortingMissionHealthCheckEvaluator(this);
        }
    }
}
