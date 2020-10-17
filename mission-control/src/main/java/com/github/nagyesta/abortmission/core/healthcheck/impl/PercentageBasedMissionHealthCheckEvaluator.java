package com.github.nagyesta.abortmission.core.healthcheck.impl;

import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;

import java.util.Objects;
import java.util.Optional;

public final class PercentageBasedMissionHealthCheckEvaluator extends AbstractMissionHealthCheckEvaluator {

    private static final double DOUBLE_100 = 100.0D;
    private static final String DEFAULT_MESSAGE_PATTERN =
            "Precondition reached threshold of %d. Abort sequence initiated.\n    Abort root cause:  %s";

    private final int burnInTestCount;
    private final int abortThreshold;
    private final String message;

    private PercentageBasedMissionHealthCheckEvaluator(final Builder builder) {
        super(Objects.requireNonNull(builder, "Builder cannot be null.").matcher, new MissionStatisticsCollector());
        this.burnInTestCount = builder.burnInTestCount;
        this.abortThreshold = builder.abortThreshold;
        this.message = Optional.ofNullable(builder.message)
                .orElseGet(() -> String.format(DEFAULT_MESSAGE_PATTERN, builder.abortThreshold, builder.matcher.getName()));
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
    public boolean shouldAbort() {
        final boolean isActive = burnInTestCount <= Math.max(getCountdownStartCount(), getCountdownCompleteCount());
        final double totalMissions = getMissionSuccessCount() + getMissionFailureCount() + getMissionAbortCount();
        final double failedOrAborted = getMissionFailureCount() + getMissionAbortCount();
        final double failurePercentage = (failedOrAborted * DOUBLE_100) / totalMissions;
        return isActive && abortThreshold < failurePercentage;
    }

    @Override
    public boolean shouldAbortCountdown() {
        final boolean isActive = burnInTestCount <= getCountdownStartCount();
        final boolean countdownNeverCompleted = getCountdownCompleteCount() == 0;
        return isActive && countdownNeverCompleted;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @SuppressWarnings({"checkstyle:HiddenField", "checkstyle:DesignForExtension"})
    public static final class Builder {
        private static final int PERCENTAGE_UPPER_LIMIT = 99;
        private static final int PERCENTAGE_LOWER_LIMIT = 0;
        private static final int BURN_IN_LOWER_LIMIT = 1;
        private final MissionHealthCheckMatcher matcher;
        private int burnInTestCount = BURN_IN_LOWER_LIMIT;
        private int abortThreshold = PERCENTAGE_LOWER_LIMIT;
        private String message;

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

        public Builder message(final String message) {
            this.message = Objects.requireNonNull(message, "Message cannot be null.");
            return this;
        }

        public PercentageBasedMissionHealthCheckEvaluator build() {
            return new PercentageBasedMissionHealthCheckEvaluator(this);
        }
    }
}
