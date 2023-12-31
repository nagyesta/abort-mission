package com.github.nagyesta.abortmission.core;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

/**
 * Global configuration for Abort-Mission.
 */
public final class AbortMissionGlobalConfiguration {

    static final int DEFAULT_STACK_TRACE_DEPTH = 10;
    static final Predicate<StackTraceElement> DEFAULT_STACK_TRACE_FILTER = e -> true;
    private static final ReentrantLock LOCK = new ReentrantLock();
    private int stackTraceDepth = DEFAULT_STACK_TRACE_DEPTH;

    private Predicate<StackTraceElement> stackTraceFilter = DEFAULT_STACK_TRACE_FILTER;

    private AbortMissionGlobalConfiguration() {
    }

    /**
     * Returns the stack trace depth.
     *
     * @return The depth of the stack trace we want to capture.
     */
    public int getStackTraceDepth() {
        return stackTraceDepth;
    }

    /**
     * Sets the stack trace depth.
     *
     * @param stackTraceDepth The depth of the stack trace we want to capture.
     */
    public void setStackTraceDepth(final int stackTraceDepth) {
        this.stackTraceDepth = stackTraceDepth;
    }

    /**
     * Returns the stack trace filter that will decide whether we want to capture a {@link StackTraceElement}.
     *
     * @return The filter we want to use to decide what to keep when capturing the stack trace.
     */
    public Predicate<StackTraceElement> getStackTraceFilter() {
        return stackTraceFilter;
    }

    /**
     * Sets the stack trace filter. Can be used to filter out unwanted stack trace elements. Cannot be null.
     *
     * @param stackTraceFilter The filter we want to use to decide what to keep when capturing the stack trace.
     */
    public void setStackTraceFilter(final Predicate<StackTraceElement> stackTraceFilter) {
        if (stackTraceFilter == null) {
            throw new IllegalArgumentException("Stack trace filter cannot be null.");
        }
        this.stackTraceFilter = stackTraceFilter;
    }

    /**
     * Convenience method returning the shared instance.
     *
     * @return the shared instance.
     */
    public static AbortMissionGlobalConfiguration shared() {
        LOCK.lock();
        try {
            return AbortMissionGlobalConfigurationHolder.SHARED_INSTANCE;
        } finally {
            LOCK.unlock();
        }
    }

    /**
     * Holder class for the shared instance.
     */
    private static final class AbortMissionGlobalConfigurationHolder {
        private static final AbortMissionGlobalConfiguration SHARED_INSTANCE = new AbortMissionGlobalConfiguration();
    }
}
