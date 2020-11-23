package com.github.nagyesta.abortmission.core.healthcheck.impl;

import com.github.nagyesta.abortmission.core.healthcheck.ReadOnlyMissionStatistics;
import com.github.nagyesta.abortmission.core.healthcheck.ReadOnlyStageStatistics;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * The component counting mission success/failure events in order to aid abort decision making.
 */
public final class MissionStatisticsCollector implements ReadOnlyMissionStatistics {

    private final StageStatisticsCollector countdown;
    private final StageStatisticsCollector mission;

    /**
     * Default constructor using 0 as baseline all across the measurements.
     * Allows clean starts.
     */
    public MissionStatisticsCollector() {
        this(new StageStatisticsCollector(), new StageStatisticsCollector());
    }

    /**
     * Constructor allowing us to set a starting value for each statistic we store.
     *
     * @param countdown The countdown specific statistics.
     * @param mission   The mission specific statistics.
     */
    public MissionStatisticsCollector(final StageStatisticsCollector countdown,
                                      final StageStatisticsCollector mission) {
        this.countdown = Objects.requireNonNull(countdown, "Countdown statistics cannot be null.");
        this.mission = Objects.requireNonNull(mission, "Mission statistics cannot be null.");
    }

    public StageStatisticsCollector getCountdown() {
        return countdown;
    }

    public StageStatisticsCollector getMission() {
        return mission;
    }

    @Override
    public ReadOnlyStageStatistics getReadOnlyCountdown() {
        return countdown;
    }

    @Override
    public ReadOnlyStageStatistics getReadOnlyMission() {
        return mission;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReadOnlyMissionStatistics)) {
            return false;
        }
        final ReadOnlyMissionStatistics that = (ReadOnlyMissionStatistics) o;
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
