package com.github.nagyesta.abortmission.core.selfpropelled;

import com.github.nagyesta.abortmission.core.AbstractLaunchSequenceTemplate;
import com.github.nagyesta.abortmission.core.MissionControl;
import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;
import com.github.nagyesta.abortmission.core.telemetry.watch.StageTimeStopwatch;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

import static com.github.nagyesta.abortmission.core.MissionControl.annotationContextEvaluator;

/**
 * Abstract superclass implementing the common parts of {@link java.util.concurrent.Callable} and
 * {@link Runnable} based implementations. This provides the template ensuring that the mission
 * stages are properly separated and their outcomes are logged.
 *
 * @param <P> The type we want to produce as the result of the test preparation and pass to the test phase.
 * @param <T> The type of the result we want to produce by the end of the test phase.
 */
public abstract class AbstractMissionTemplate<P, T> extends AbstractLaunchSequenceTemplate implements PreLaunchMissionTemplate<P> {

    private static final BiFunction<String, Class<?>, Set<MissionHealthCheckEvaluator>> CLASS_ONLY_EVALUATOR_LOOKUP =
            (context, type) -> Optional.ofNullable(context)
                    .map(MissionControl::commandOps).orElseGet(MissionControl::commandOps)
                    .matchingEvaluators(type);
    private final Class<?> evaluationScope;
    private final String contextName;

    /**
     * Constructor setting the test context name used for {@link com.github.nagyesta.abortmission.core.AbortMissionCommandOps}
     * resolution, the evaluation scope we want to use for our matchers and the abort sequence to use in case we want to abort.
     *
     * @param contextName     The name of the mission context
     * @param evaluationScope The class we want to use as the annotated test class which will be used by matchers.
     * @param abortSequence   The {@link Runnable} abort sequence (typically throwing an exception).
     */
    protected AbstractMissionTemplate(
            final String contextName,
            final Class<?> evaluationScope,
            final Runnable abortSequence) {
        super(abortSequence, type -> CLASS_ONLY_EVALUATOR_LOOKUP.apply(contextName, type));
        this.contextName = contextName;
        this.evaluationScope = Objects.requireNonNull(evaluationScope, "Test class cannot be null.");
    }

    /**
     * Executes the template.
     *
     * @return The end result of the test (optional)
     */
    protected final Optional<T> executeTemplate() {
        final var countdownStopWatch = performPreLaunchInit(evaluationScope, evaluationScope.getSimpleName());
        final var preparedContext = executePreLaunchPreparation(countdownStopWatch);
        return executeLaunch(preparedContext);
    }

    /**
     * Defines how the launch should look like.
     *
     * @param preparedContext The prepared context we received from the preparation step.
     * @return the test result (optional)
     */
    protected abstract Optional<T> doLaunch(P preparedContext);

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private P executePreLaunchPreparation(final Optional<StageTimeStopwatch> countdownStopWatch) {
        final P preparedContext;
        try {
            preparedContext = preLaunchPreparationSupplier().get();
            countdownCompletedSuccessfully(getClassLevelEvaluatorsOnly(), countdownStopWatch);
        } catch (final Exception e) {
            countdownFailureDetected(getClassLevelEvaluatorsOnly(), countdownStopWatch, Optional.of(e),
                    annotationContextEvaluator().findSuppressedExceptions(evaluationScope));
            throw e;
        }
        return preparedContext;
    }

    private Optional<T> executeLaunch(final P preparedContext) {
        final var stopwatch = evaluateLaunchAbort(
                getClassLevelEvaluatorsOnly(), new StageTimeStopwatch(evaluationScope.getName(), "executeLaunch"),
                () -> annotationContextEvaluator().isAbortSuppressed(evaluationScope)
        );
        try {
            final var result = doLaunch(preparedContext);
            missionCompletedSuccessfully(getClassLevelEvaluatorsOnly(), stopwatch);
            return result;
        } catch (final Exception e) {
            missionFailureDetected(getClassLevelEvaluatorsOnly(), stopwatch,
                    Optional.of(e), annotationContextEvaluator().findSuppressedExceptions(evaluationScope)
            );
            throw e;
        }
    }

    private Set<MissionHealthCheckEvaluator> getClassLevelEvaluatorsOnly() {
        return CLASS_ONLY_EVALUATOR_LOOKUP.apply(contextName, evaluationScope);
    }

}
