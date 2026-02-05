package com.github.nagyesta.abortmission.core.healthcheck.impl;

import com.github.nagyesta.abortmission.core.healthcheck.ReadOnlyMissionStatistics;
import com.github.nagyesta.abortmission.core.healthcheck.ReadOnlyStageStatistics;
import com.github.nagyesta.abortmission.core.healthcheck.StageStatistics;
import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * The component counting mission success/failure events to aid abort decision-making.
 */
public final class MissionStatisticsCollector implements ReadOnlyMissionStatistics {

    private final StageStatistics countdown;
    private final StageStatistics mission;

    /**
     * Default constructor using 0 as baseline all across the measurements.
     * Allows clean starts.
     *
     * @param matcher The matcher used by the evaluator owning this collector.
     */
    public MissionStatisticsCollector(final MissionHealthCheckMatcher matcher) {
        this(new StageStatisticsCollector(matcher), new StageStatisticsCollector(matcher));
    }

    /**
     * Constructor allowing us to set a starting value for each statistic we store.
     *
     * @param countdown The countdown-specific statistics.
     * @param mission   The mission-specific statistics.
     */
    public MissionStatisticsCollector(
            final StageStatistics countdown,
            final StageStatistics mission) {
        this.countdown = Objects.requireNonNull(countdown, "Countdown statistics cannot be null.");
        this.mission = Objects.requireNonNull(mission, "Mission statistics cannot be null.");
    }

    public StageStatistics getCountdown() {
        return countdown;
    }

    public StageStatistics getMission() {
        return mission;
    }

    @Override
    public ReadOnlyStageStatistics getReadOnlyCountdown() {
        return getCountdown();
    }

    @Override
    public ReadOnlyStageStatistics getReadOnlyMission() {
        return getMission();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final ReadOnlyMissionStatistics that)) {
            return false;
        }
        return Objects.equals(countdown, that.getReadOnlyCountdown())
                && Objects.equals(mission, that.getReadOnlyMission());
    }

    @Override
    public int hashCode() {
        return Objects.hash(countdown, mission);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MissionStatisticsCollector.class.getSimpleName() + "[", "]")
                .add("countdown=" + countdown)
                .add("mission=" + mission)
                .toString();
    }
}
