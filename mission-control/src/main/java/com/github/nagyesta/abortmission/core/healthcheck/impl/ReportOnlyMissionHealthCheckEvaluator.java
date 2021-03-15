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
        super(Objects.requireNonNull(builder, "Builder cannot be null.").matcher, builder.statisticsCollector);
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
        return false;
    }

    @Override
    protected boolean shouldAbortCountdownInternal() {
        return false;
    }

    @SuppressWarnings({"checkstyle:HiddenField", "checkstyle:DesignForExtension"})
    public static final class Builder {
        private final MissionHealthCheckMatcher matcher;
        private final MissionStatisticsCollector statisticsCollector;

        private Builder(final MissionHealthCheckMatcher matcher,
                        final MissionStatisticsCollector statisticsCollector) {
            this.matcher = Objects.requireNonNull(matcher, "Matcher cannot be null.");
            this.statisticsCollector = Objects.requireNonNull(statisticsCollector, "Statistic collector cannot be null.");
        }

        public ReportOnlyMissionHealthCheckEvaluator build() {
            return new ReportOnlyMissionHealthCheckEvaluator(this);
        }
    }
}
