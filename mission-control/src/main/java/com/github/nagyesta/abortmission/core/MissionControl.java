package com.github.nagyesta.abortmission.core;

import com.github.nagyesta.abortmission.core.annotation.AnnotationContextEvaluator;
import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;
import com.github.nagyesta.abortmission.core.healthcheck.impl.PercentageBasedMissionHealthCheckEvaluator;
import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;
import com.github.nagyesta.abortmission.core.matcher.impl.MissionHealthCheckMatcherBuilder;
import com.github.nagyesta.abortmission.core.matcher.impl.builder.InitialMissionHealthCheckMatcherBuilder;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Provides shorthands for the core functionality of the library.
 */
public final class MissionControl {

    private MissionControl() {
        throw new UnsupportedOperationException("Util class.");
    }

    /**
     * Instantiates a {@link MissionHealthCheckMatcher} builder.
     *
     * @return builder
     */
    public static InitialMissionHealthCheckMatcherBuilder matcher() {
        return MissionHealthCheckMatcherBuilder.builder();
    }

    /**
     * Creates a builder instance for percentage based evaluators.
     *
     * @param matcher The matcher we want to use for this evaluator.
     * @return builder
     */
    public static PercentageBasedMissionHealthCheckEvaluator.Builder percentageBasedEvaluator(final MissionHealthCheckMatcher matcher) {
        return PercentageBasedMissionHealthCheckEvaluator.builder(matcher);
    }

    /**
     * Created and initializes a shared {@link AbortMissionCommandOps}.
     *
     * @param initMissionOutline The function we can use to define the evaluators.
     */
    public static void createSharedCommandOps(final Function<AbortMissionCommandOps, AbortMissionCommandOps> initMissionOutline) {
        if (commandOps() != null) {
            throw new IllegalStateException("Shared instance is already created.");
        }
        initMissionOutline.apply(AbortMissionCommandOps.newInstance()).finalizeSetupAsShared();
    }

    /**
     * Created and initializes a named {@link AbortMissionCommandOps}.
     *
     * @param name               The context name we want to use.
     * @param initMissionOutline The function we can use to define the evaluators.
     */
    public static void createCommandOps(final String name,
                                        final Function<AbortMissionCommandOps, AbortMissionCommandOps> initMissionOutline) {
        if (commandOps(name) != null) {
            throw new IllegalStateException("Named context instance is already created.");
        }
        initMissionOutline.apply(AbortMissionCommandOps.newInstance()).finalizeSetup(name);
    }

    /**
     * Returns the shared {@link AbortMissionCommandOps} instance (if it was initialized before).
     *
     * @return the shared instance or null if it was not registered yet.
     */
    public static AbortMissionCommandOps commandOps() {
        return AbortMissionCommandOps.shared();
    }

    /**
     * Returns the {@link AbortMissionCommandOps} instance identified by the context name provided (if it was initialized before).
     *
     * @param name The context name.
     * @return the named instance or null if it was not registered yet.
     */
    public static AbortMissionCommandOps commandOps(final String name) {
        return AbortMissionCommandOps.named(name);
    }

    /**
     * Returns an {@link AnnotationContextEvaluator} instance (singleton).
     *
     * @return shared instance
     */
    public static AnnotationContextEvaluator annotationContextEvaluator() {
        return AnnotationContextEvaluator.shared();
    }

    /**
     * Returns the {@link MissionHealthCheckEvaluator} instances which are registered with the shared command ops
     * and are matching the component received as parameter.
     *
     * @param component The component we want to match.
     * @return set of matching evaluators
     */
    public static Set<MissionHealthCheckEvaluator> matchingHealthChecks(final Object component) {
        return Optional.ofNullable(AbortMissionCommandOps.shared())
                .map(ops -> ops.matchingEvaluators(component))
                .orElse(Collections.emptySet());
    }

    /**
     * Returns the {@link MissionHealthCheckEvaluator} instances which are registered with the named command ops
     * identified by the provided name and are matching the component received as parameter.
     *
     * @param contextName The context name.
     * @param component   The component we want to match.
     * @return set of matching evaluators
     */
    public static Set<MissionHealthCheckEvaluator> matchingHealthChecks(final String contextName, final Object component) {
        return AbortMissionCommandOps.named(contextName).matchingEvaluators(component);
    }
}
