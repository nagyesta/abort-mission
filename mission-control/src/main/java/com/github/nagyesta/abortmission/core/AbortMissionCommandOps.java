package com.github.nagyesta.abortmission.core;

import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;
import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * The core class which acts as a blast radius/context by holding the matchers and evaluators specific to this
 * logical test group.
 */
public final class AbortMissionCommandOps {

    private static final ReentrantLock LOCK = new ReentrantLock();
    private final Map<MissionHealthCheckMatcher, MissionHealthCheckEvaluator> status = new TreeMap<>();
    private final AtomicBoolean finalized = new AtomicBoolean(false);

    /**
     * Convenience method creating a new instance.
     *
     * @return a new {@link AbortMissionCommandOps}.
     */
    public static AbortMissionCommandOps newInstance() {
        return new AbortMissionCommandOps();
    }

    /**
     * Convenience method returning the shared instance (default context).
     *
     * @return the shared instance.
     */
    public static AbortMissionCommandOps shared() {
        return MissionControlHolder.NAMED_CONTEXTS.get(MissionControlHolder.SHARED_INSTANCE);
    }

    /**
     * Convenience method returning the named instance registered with the known contexts.
     *
     * @param name The name of the named context. Name will be trimmed, empty/null is not allowed.
     * @return the named context
     */
    public static AbortMissionCommandOps named(final String name) {
        final String trimmedName = sanitizeKey(name);
        return MissionControlHolder.NAMED_CONTEXTS.get(trimmedName);
    }

    private static String sanitizeKey(final String name) {
        Objects.requireNonNull(name, "Context name cannot be null.");
        final String trimmedName = name.trim();
        if (trimmedName.isEmpty()) {
            throw new IllegalArgumentException("Context name cannot be empty.");
        }
        return trimmedName;
    }

    /**
     * Returns the names of all registered contexts.
     *
     * @return context names
     */
    public static Set<String> contextNames() {
        return Collections.unmodifiableSet(MissionControlHolder.NAMED_CONTEXTS.keySet());
    }

    /**
     * Registers a new evaluator with the current instance.
     *
     * @param evaluator The evaluator we want to register.
     */
    public void registerHealthCheck(final MissionHealthCheckEvaluator evaluator) {
        Objects.requireNonNull(evaluator, "Null evaluator cannot be registered.");
        if (finalized.get()) {
            throw new IllegalStateException("Context is already finalized, no update permitted.");
        }
        if (status.containsKey(evaluator.getMatcher())) {
            throw new IllegalStateException("An evaluator is already registered for this matcher: " + evaluator.getMatcher());
        }
        status.put(evaluator.getMatcher(), evaluator);
    }

    /**
     * Finalizes the setup of the current instance as shared instance.
     */
    public void finalizeSetupAsShared() {
        doFinalizeSetup(MissionControlHolder.SHARED_INSTANCE);
    }

    /**
     * Finalizes the setup of the current instance as a named instance.
     *
     * @param contextName The name of the context.
     */
    public void finalizeSetup(final String contextName) {
        doFinalizeSetup(sanitizeKey(contextName));
    }

    /**
     * Finds the evaluators which are registered with the current context and are matching the provided component.
     *
     * @param component The component we need to match against.
     * @return The matching evaluators.
     */
    public Set<MissionHealthCheckEvaluator> matchingEvaluators(final Object component) {
        return status.entrySet().stream()
                .filter(e -> e.getKey().matches(component))
                .map(Map.Entry::getValue)
                .collect(Collectors.toSet());
    }

    /**
     * Returns all registered evaluators of the current context.
     *
     * @return registered evaluators.
     */
    @SuppressWarnings("unused")
    public Collection<MissionHealthCheckEvaluator> allEvaluators() {
        return Collections.unmodifiableCollection(status.values());
    }

    private void doFinalizeSetup(final String contextName) {
        LOCK.lock();
        try {
            MissionControlHolder.NAMED_CONTEXTS.putIfAbsent(contextName, this);
            this.finalized.set(true);
        } finally {
            LOCK.unlock();
        }
    }

    private static final class MissionControlHolder {
        private static final String SHARED_INSTANCE = "";
        private static final Map<String, AbortMissionCommandOps> NAMED_CONTEXTS = new TreeMap<>();
    }

}
