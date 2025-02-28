package com.github.nagyesta.abortmission.core.healthcheck.impl;

import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;
import com.github.nagyesta.abortmission.core.healthcheck.ReadOnlyMissionStatistics;
import com.github.nagyesta.abortmission.core.healthcheck.ReadOnlyStageStatistics;
import com.github.nagyesta.abortmission.core.healthcheck.StatisticsLogger;
import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.nagyesta.abortmission.core.MissionControl.*;

/**
 * Implements the common functionality of {@link MissionHealthCheckEvaluator} instances.
 */
public abstract class AbstractMissionHealthCheckEvaluator implements MissionHealthCheckEvaluator {

    private final MissionHealthCheckMatcher matcher;
    private final MissionStatisticsCollector stats;
    private final String overrideKeyword;

    /**
     * Sets the matcher and the mission statistics collector.
     *
     * @param matcher The health check matcher mentioned by {@link MissionHealthCheckEvaluator#getMatcher()}.
     * @param stats   The statistics collector mentioned by {@link MissionHealthCheckEvaluator#getStats()}.
     */
    protected AbstractMissionHealthCheckEvaluator(final MissionHealthCheckMatcher matcher,
                                                  final MissionStatisticsCollector stats) {
        this(matcher, stats, null);
    }

    /**
     * Sets the matcher and the mission statistics collector.
     *
     * @param matcher         The health check matcher mentioned by {@link MissionHealthCheckEvaluator#getMatcher()}.
     * @param stats           The statistics collector mentioned by {@link MissionHealthCheckEvaluator#getStats()}.
     * @param overrideKeyword The keyword used for fine-grained abort/disarm overrides.
     */
    protected AbstractMissionHealthCheckEvaluator(final MissionHealthCheckMatcher matcher,
                                                  final MissionStatisticsCollector stats,
                                                  final String overrideKeyword) {
        this.matcher = matcher;
        this.stats = stats;
        this.overrideKeyword = overrideKeyword;
    }

    @Override
    public String overrideKeyword() {
        return overrideKeyword;
    }

    @Override
    public ReadOnlyMissionStatistics getStats() {
        return this.stats;
    }

    @Override
    public MissionHealthCheckMatcher getMatcher() {
        return matcher;
    }

    @Override
    public ReadOnlyStageStatistics getCountdownStatistics() {
        return stats.getReadOnlyCountdown();
    }

    @Override
    public StatisticsLogger countdownLogger() {
        return stats.getCountdown();
    }

    @Override
    public ReadOnlyStageStatistics getMissionStatistics() {
        return stats.getReadOnlyMission();
    }

    @Override
    public StatisticsLogger missionLogger() {
        return stats.getMission();
    }

    @Override
    public boolean shouldSuppressAbort() {
        return evaluateOverrideList(ABORT_MISSION_SUPPRESS_ABORT_EVALUATORS)
                .contains(overrideKeyword);
    }

    @Override
    public boolean shouldForceAbort() {
        return evaluateOverrideList(ABORT_MISSION_FORCE_ABORT_EVALUATORS)
                .contains(overrideKeyword);
    }

    @Override
    public boolean shouldAbort() {
        if (isDisarmed(ABORT_MISSION_DISARM_MISSION)) {
            return false;
        }
        return shouldForceAbort() || shouldAbortInternal();
    }

    @Override
    public boolean shouldAbortCountdown() {
        if (isDisarmed(ABORT_MISSION_DISARM_COUNTDOWN)) {
            return false;
        }
        return shouldForceAbort() || shouldAbortCountdownInternal();
    }

    /**
     * Returns the set of evaluator keywords which are defined in the given System property.
     *
     * @param propertyName The name of the System property.
     * @return The tokenized set of keywords defined by the property value or empty set if not defined.
     */
    protected Set<String> evaluateOverrideList(final String propertyName) {
        final var property = System.getProperty(propertyName, "");
        return Arrays.stream(property.split(","))
                .filter(s -> !s.isBlank())
                .collect(Collectors.toSet());
    }

    /**
     * Determines whether the disarm-switches are set or not.
     *
     * @param switchName The property name of the switch.
     * @return true is the switch is true (a.k.a. disarmed) false otherwise
     */
    protected boolean isDisarmed(final String switchName) {
        return Boolean.TRUE.toString().equalsIgnoreCase(System.getProperty(switchName));
    }

    /**
     * Decides whether the current implementation needs to abort the mission.
     *
     * @return true if the implementing class decides to abort the mission, false otherwise
     */
    protected abstract boolean shouldAbortInternal();

    /**
     * Decides whether the current implementation needs to abort the countdown.
     *
     * @return true if the implementing class decides to abort the countdown, false otherwise
     */
    protected abstract boolean shouldAbortCountdownInternal();
}
