package com.github.nagyesta.abortmission.core.healthcheck.impl;

import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;

import java.util.Objects;

/**
 * {@link com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator} implementation based on failure to success
 * percentage thresholds.
 */
@SuppressWarnings("checkstyle:FinalClass")
public class PercentageBasedMissionHealthCheckEvaluator extends AbstractMissionHealthCheckEvaluator {

    private static final double DOUBLE_100 = 100.0D;

    private final int burnInTestCount;
    private final int abortThreshold;

    private PercentageBasedMissionHealthCheckEvaluator(final Builder builder) {
        super(Objects.requireNonNull(builder, "Builder cannot be null.").matcher, new MissionStatisticsCollector());
        this.burnInTestCount = builder.burnInTestCount;
        this.abortThreshold = builder.abortThreshold;
    }

    public static Builder builder(final MissionHealthCheckMatcher matcher) {
        return new Builder(matcher);
    }

    @Override
    public int getBurnInTestCount() {
        return burnInTestCount;
    }

    public int getAbortThreshold() {
        return abortThreshold;
    }

    @Override
    protected boolean shouldAbortInternal() {
        final double totalMissions = getMissionStatistics().getTotal();
        final boolean isActive = burnInTestCount <= totalMissions;
        final double failedOrAborted = getMissionStatistics().getNotSuccessful();
        final double failurePercentage = (failedOrAborted * DOUBLE_100) / totalMissions;
        return isActive && abortThreshold < failurePercentage;
    }

    @Override
    protected boolean shouldAbortCountdownInternal() {
        final boolean isActive = burnInTestCount <= getCountdownStatistics().getTotal();
        final boolean countdownNeverCompleted = getCountdownStatistics().getSucceeded() == 0;
        return isActive && countdownNeverCompleted;
    }

    @SuppressWarnings({"checkstyle:HiddenField", "checkstyle:DesignForExtension"})
    public static final class Builder {
        private static final int PERCENTAGE_UPPER_LIMIT = 99;
        private static final int PERCENTAGE_LOWER_LIMIT = 0;
        private static final int BURN_IN_LOWER_LIMIT = 1;
        private final MissionHealthCheckMatcher matcher;
        private int burnInTestCount = BURN_IN_LOWER_LIMIT;
        private int abortThreshold = PERCENTAGE_LOWER_LIMIT;

        private Builder(final MissionHealthCheckMatcher matcher) {
            this.matcher = Objects.requireNonNull(matcher, "Matcher cannot be null.");
        }

        public Builder burnInTestCount(final int burnInTestCount) {
            if (burnInTestCount < BURN_IN_LOWER_LIMIT) {
                throw new IllegalArgumentException("Burn-in threshold must be >= 1.");
            }
            this.burnInTestCount = burnInTestCount;
            return this;
        }

        public Builder abortThreshold(final int abortThreshold) {
            if (abortThreshold < PERCENTAGE_LOWER_LIMIT || abortThreshold > PERCENTAGE_UPPER_LIMIT) {
                throw new IllegalArgumentException("Abort threshold must be in the 0-99 range (inclusive).");
            }
            this.abortThreshold = abortThreshold;
            return this;
        }

        public PercentageBasedMissionHealthCheckEvaluator build() {
            return new PercentageBasedMissionHealthCheckEvaluator(this);
        }
    }
}
