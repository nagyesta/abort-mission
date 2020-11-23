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
        final ExtensionContext parentOrCurrentContext = findClassContext(context).orElse(context);
        final Optional<StageTimeStopwatch> stopwatch = optionalStopwatch(parentOrCurrentContext, COUNTDOWN_START);
        final Optional<Throwable> throwable = context.getExecutionException()
                .filter(e -> !(e instanceof TestAbortedException));
        if (throwable.isPresent()) {
            context.getTestClass().ifPresent(testClass -> launchSequenceTemplate.countdownFailure(testClass, throwable, stopwatch));
        }
        putOptionalStopwatch(parentOrCurrentContext, Optional.empty(), COUNTDOWN_START);
    }

    @Override
    public void testFailed(final ExtensionContext context, final Throwable throwable) {
        final Optional<StageTimeStopwatch> stopwatch = optionalStopwatch(context, MISSION_START);
        final Optional<Throwable> rootCause = Optional.of(throwable);
        if (!stopwatch.isPresent()) {
            findClassContext(context).ifPresent(classContext -> {
                final Optional<StageTimeStopwatch> countdownStopwatch = optionalStopwatch(classContext, COUNTDOWN_START);
                classContext.getTestClass().ifPresent(testClass -> {
                    launchSequenceTemplate.countdownFailure(testClass, rootCause, countdownStopwatch);
                    putOptionalStopwatch(classContext, Optional.empty(), COUNTDOWN_START);
                });
            });
        }
        context.getTestMethod().ifPresent(method -> launchSequenceTemplate.launchFailure(method, rootCause, stopwatch));
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
        findClassContext(context).ifPresent(this::markCountdownSuccessful);
        context.getTestMethod().ifPresent(method -> {
            final Optional<StageTimeStopwatch> stopwatch = launchSequenceTemplate.launchImminent(method);
            putOptionalStopwatch(context, stopwatch, MISSION_START);
        });
    }

    private Optional<ExtensionContext> findClassContext(final ExtensionContext context) {
        final Optional<ExtensionContext> parent = context.getParent();
        //if the test class is not set, we went too far up the chain
        if (!context.getTestClass().isPresent()) {
            return Optional.empty();
        }
        //fall back either when recursion turned up empty or when we have no parent
        return parent.map(recursion -> Optional.of(findClassContext(recursion).orElse(context)))
                .orElseGet(() -> Optional.of(context));
    }

    private void markCountdownSuccessful(final ExtensionContext classContext) {
        final ExtensionContext.Store parentStore = classContext.getStore(NAMESPACE);
        final boolean perClassMode = classContext.getTestInstanceLifecycle().filter(l -> l == PER_CLASS).isPresent();
        final boolean perMethodMode = classContext.getTestInstanceLifecycle().filter(l -> l == PER_METHOD).isPresent();
        final boolean firstRun = parentStore.get(COUNTDOWN_SUCCESS, Boolean.class) == null;
        if ((perClassMode && firstRun) || perMethodMode) {
            classContext.getTestClass().ifPresent(testClass -> {
                final Optional<StageTimeStopwatch> stopwatch = optionalStopwatch(classContext, COUNTDOWN_START);
                launchSequenceTemplate.countdownSuccess(testClass, stopwatch);
                putOptionalStopwatch(classContext, Optional.empty(), COUNTDOWN_START);
            });
            final boolean failed = classContext.getExecutionException()
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
