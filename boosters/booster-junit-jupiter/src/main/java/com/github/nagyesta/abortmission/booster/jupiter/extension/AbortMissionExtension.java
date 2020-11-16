package com.github.nagyesta.abortmission.booster.jupiter.extension;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import com.github.nagyesta.abortmission.core.LaunchSequenceTemplate;
import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;
import com.github.nagyesta.abortmission.core.telemetry.watch.StageTimeStopwatch;
import org.junit.jupiter.api.extension.*;
import org.opentest4j.TestAbortedException;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;

import static com.github.nagyesta.abortmission.core.MissionControl.annotationContextEvaluator;
import static com.github.nagyesta.abortmission.core.MissionControl.matchingHealthChecks;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_METHOD;

public class AbortMissionExtension implements TestInstancePostProcessor, TestWatcher, BeforeEachCallback, TestInstancePreDestroyCallback {

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace
            .create("com", "github", "nagyesta", "Abort-Mission", "telemetry");
    private static final String COUNTDOWN_START = "countdown_start";
    private static final String MISSION_START = "mission_start";
    private static final String COUNTDOWN_SUCCESS = "countdown_success";
    private final LaunchSequenceTemplate launchSequenceTemplate =
            new LaunchSequenceTemplate(this::doAbort, this::findEvaluators, this::findEvaluators);

    @SuppressWarnings("RedundantThrows")
    @Override
    public void postProcessTestInstance(final Object testInstance, final ExtensionContext context) throws Exception {
        final Optional<StageTimeStopwatch> stopwatch = launchSequenceTemplate.launchGoNoGo(testInstance.getClass());
        putOptionalStopwatch(context, stopwatch, COUNTDOWN_START);
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public void preDestroyTestInstance(final ExtensionContext context) throws Exception {
        final Boolean success = context.getStore(NAMESPACE).get(COUNTDOWN_SUCCESS, Boolean.class);
        if (success == Boolean.TRUE) {
            return;
        }
        final Optional<StageTimeStopwatch> stopwatch = optionalStopwatch(context, COUNTDOWN_START);
        final Optional<Throwable> throwable = context.getExecutionException()
                .filter(e -> !(e instanceof TestAbortedException));
        if (throwable.isPresent()) {
            context.getTestClass().ifPresent(testClass -> launchSequenceTemplate.countdownFailure(testClass, throwable, stopwatch));
        }
    }

    @Override
    public void testFailed(final ExtensionContext context, final Throwable throwable) {
        final Optional<StageTimeStopwatch> stopwatch = optionalStopwatch(context, MISSION_START);
        context.getTestMethod().ifPresent(method -> launchSequenceTemplate.launchFailure(method, Optional.of(throwable), stopwatch));
    }

    @Override
    public void testSuccessful(final ExtensionContext context) {
        context.getTestMethod().ifPresent(method -> {
            final Optional<StageTimeStopwatch> stopwatch = optionalStopwatch(context, MISSION_START);
            launchSequenceTemplate.launchSuccess(method, stopwatch);
        });
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        context.getParent().ifPresent(parent -> markCountdownSuccessful(context, parent));
        context.getTestMethod().ifPresent(method -> {
            final Optional<StageTimeStopwatch> stopwatch = launchSequenceTemplate.launchImminent(method);
            putOptionalStopwatch(context, stopwatch, MISSION_START);
        });
    }

    private void markCountdownSuccessful(final ExtensionContext context, final ExtensionContext parent) {
        final ExtensionContext.Store parentStore = parent.getStore(NAMESPACE);
        final boolean perClassMode = context.getTestInstanceLifecycle().filter(l -> l == PER_CLASS).isPresent();
        final boolean perMethodMode = context.getTestInstanceLifecycle().filter(l -> l == PER_METHOD).isPresent();
        final boolean firstRun = parentStore.get(COUNTDOWN_SUCCESS, Boolean.class) == null;
        if ((perClassMode && firstRun) || perMethodMode) {
            context.getTestClass().ifPresent(testClass -> {
                final Optional<StageTimeStopwatch> stopwatch = optionalStopwatch(parent, COUNTDOWN_START);
                launchSequenceTemplate.countdownSuccess(testClass, stopwatch);
            });
            final boolean failed = context.getExecutionException()
                    .filter(e -> !(e instanceof TestAbortedException))
                    .isPresent();
            parentStore.put(COUNTDOWN_SUCCESS, !failed);
        }
    }

    private void doAbort() {
        throw new TestAbortedException("Aborting test as mission checks indicate failure.");
    }

    private Set<MissionHealthCheckEvaluator> findEvaluators(final Method method) {
        return annotationContextEvaluator().findContextName(method, LaunchAbortArmed.class, LaunchAbortArmed::value)
                .map(name -> matchingHealthChecks(name, method))
                .orElseGet(() -> matchingHealthChecks(method));
    }

    private Set<MissionHealthCheckEvaluator> findEvaluators(final Class<?> testClass) {
        return annotationContextEvaluator().findContextName(testClass, LaunchAbortArmed.class, LaunchAbortArmed::value)
                .map(name -> matchingHealthChecks(name, testClass))
                .orElseGet(() -> matchingHealthChecks(testClass));
    }

    private Optional<StageTimeStopwatch> optionalStopwatch(final ExtensionContext context, final String key) {
        return Optional.ofNullable(context.getStore(NAMESPACE).getOrDefault(key, StageTimeStopwatch.class, null));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private void putOptionalStopwatch(final ExtensionContext context, final Optional<StageTimeStopwatch> stopwatch, final String key) {
        if (stopwatch.isPresent()) {
            context.getStore(NAMESPACE).put(key, stopwatch.get());
        } else {
            context.getStore(NAMESPACE).remove(key);
        }
    }
}
