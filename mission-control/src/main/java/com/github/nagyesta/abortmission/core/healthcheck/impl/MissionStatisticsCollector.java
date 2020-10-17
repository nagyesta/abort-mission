package com.github.nagyesta.abortmission.core.healthcheck.impl;

import com.github.nagyesta.abortmission.core.healthcheck.MissionStatisticsView;

import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The component counting mission success/failure events in order to aid abort decision making.
 */
public final class MissionStatisticsCollector implements MissionStatisticsView {

    private final AtomicInteger countdownStarted;
    private final AtomicInteger countdownCompleted;
    private final AtomicInteger countdownAborted;
    private final AtomicInteger missionSuccess;
    private final AtomicInteger missionFailure;
    private final AtomicInteger missionAbort;

    /**
     * Default constructor using 0 as baseline all across the measurements.
     * Allows clean starts.
     */
    public MissionStatisticsCollector() {
        this(0, 0, 0, 0, 0, 0);
    }

    /**
     * Constructor allowing us to start with some previous knowledge about successes or failures.
     *
     * @param countdownStarted   The number of times test preparation was started.
     * @param countdownCompleted The number of times test preparation was completed successfully.
     * @param countdownAborted   The number of times test preparation was aborted.
     * @param missionSuccess     The number of times test run succeeded.
     * @param missionFailure     The number of times test preparation or test run failed.
     * @param missionAbort       The number of times test run was aborted.
     */
    public MissionStatisticsCollector(final int countdownStarted, final int countdownCompleted,
                                      final int countdownAborted, final int missionSuccess,
                                      final int missionFailure, final int missionAbort) {
        this.countdownStarted = new AtomicInteger(countdownStarted);
        this.countdownCompleted = new AtomicInteger(countdownCompleted);
        this.countdownAborted = new AtomicInteger(countdownAborted);
        this.missionSuccess = new AtomicInteger(missionSuccess);
        this.missionFailure = new AtomicInteger(missionFailure);
        this.missionAbort = new AtomicInteger(missionAbort);
    }

    @Override
    public int getCountdownStarted() {
        return countdownStarted.get();
    }

    @Override
    public int getCountdownCompleted() {
        return countdownCompleted.get();
    }

    @Override
    public int getCountdownAborted() {
        return countdownAborted.get();
    }

    @Override
    public int getMissionSuccess() {
        return missionSuccess.get();
    }

    @Override
    public int getMissionFailure() {
        return missionFailure.get();
    }

    @Override
    public int getMissionAbort() {
        return missionAbort.get();
    }

    public void incrementCountdownStarted() {
        countdownStarted.incrementAndGet();
    }

    public void incrementCountdownCompleted() {
        countdownCompleted.incrementAndGet();
    }

    public void incrementCountdownAborted() {
        countdownAborted.incrementAndGet();
    }

    public void incrementMissionSuccess() {
        missionSuccess.incrementAndGet();
    }

    public void incrementMissionFailure() {
        missionFailure.incrementAndGet();
    }

    public void incrementMissionAbort() {
        missionAbort.incrementAndGet();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MissionStatisticsView)) {
            return false;
        }
        final MissionStatisticsView that = (MissionStatisticsView) o;
        return countdownStarted.get() == that.getCountdownStarted()
                && countdownCompleted.get() == that.getCountdownCompleted()
                && countdownAborted.get() == that.getCountdownAborted()
                && missionSuccess.get() == that.getMissionSuccess()
                && missionFailure.get() == that.getMissionFailure()
                && missionAbort.get() == that.getMissionAbort();
    }

    @Override
    public int hashCode() {
        return Objects.hash(countdownStarted.get(), countdownCompleted.get(), countdownAborted.get(),
                missionSuccess.get(), missionFailure.get(), missionAbort.get());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MissionStatisticsCollector.class.getSimpleName() + "[", "]")
                .add("countdownStarted=" + countdownStarted)
                .add("countdownCompleted=" + countdownCompleted)
                .add("countdownAborted=" + countdownAborted)
                .add("missionSuccess=" + missionSuccess)
                .add("missionFailure=" + missionFailure)
                .add("missionAbort=" + missionAbort)
                .toString();
    }
}
