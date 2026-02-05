package com.github.nagyesta.abortmission.booster.jupiter.extension;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import com.github.nagyesta.abortmission.core.LaunchSequenceTemplate;
import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;
import com.github.nagyesta.abortmission.core.telemetry.watch.StageTimeStopwatch;
import org.junit.jupiter.api.extension.*;
import org.opentest4j.TestAbortedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;

import static com.github.nagyesta.abortmission.core.MissionControl.annotationContextEvaluator;
import static com.github.nagyesta.abortmission.core.MissionControl.matchingHealthChecks;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_METHOD;

public class AbortMissionExtension implements TestInstancePostProcessor, TestWatcher, BeforeEachCallback, TestInstancePreDestroyCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbortMissionExtension.class);
    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace
            .create("com", "github", "nagyesta", "Abort-Mission", "telemetry");
    private static final String COUNTDOWN_START_PREFIX = "countdown_start-";
    private static final String MISSION_START_PREFIX = "mission_start-";
    private static final String COUNTDOWN_SUCCESS_PREFIX = "countdown_success-";
    private static final String CLASS_LEVEL_MARKER_PREFIX = "class_level-";
    private final LaunchSequenceTemplate launchSequenceTemplate =
            new LaunchSequenceTemplate(this::doAbort, this::findEvaluators, this::findEvaluators);

    @SuppressWarnings("RedundantThrows")
    @Override
    public void postProcessTestInstance(
            final Object testInstance,
            final ExtensionContext context) throws Exception {
        final var stopwatch = launchSequenceTemplate
                .launchGoNoGo(testInstance.getClass(), getDisplayNameOfContext(context));
        LOGGER.trace("Post-processing test instance of class: {} for launch id: {}",
                testInstance.getClass(), stopwatch.map(StageTimeStopwatch::getLaunchId).orElse(null));
        context.getStore(NAMESPACE).put(CLASS_LEVEL_MARKER_PREFIX + Thread.currentThread().getName(), testInstance.getClass());
        putOptionalStopwatch(context, stopwatch, COUNTDOWN_START_PREFIX + Thread.currentThread().getName());
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public void preDestroyTestInstance(final ExtensionContext context) throws Exception {
        final var success = context.getStore(NAMESPACE).get(COUNTDOWN_SUCCESS_PREFIX + Thread.currentThread().getName(), Boolean.class);
        if (success == Boolean.TRUE) {
            final var className = context.getTestClass().map(Class::getSimpleName).orElse(null);
            LOGGER.trace("Cleaning up test instance of class: {} where no reporting is necessary", className);
            return;
        }
        final var parentOrCurrentContext = findClassContext(context).orElse(context);
        final var stopwatch =
                optionalStopwatch(parentOrCurrentContext, COUNTDOWN_START_PREFIX + Thread.currentThread().getName());
        final var throwable = context.getExecutionException()
                .filter(e -> !(e instanceof TestAbortedException));
        if (throwable.isPresent()) {
            context.getTestClass().ifPresent(testClass -> launchSequenceTemplate.countdownFailure(testClass, throwable, stopwatch));
            final var className = context.getTestClass().map(Class::getSimpleName).orElse(null);
            LOGGER.trace("Cleaning up test instance of class: {} and reporting failure due to cause: {}",
                    className, throwable.get().getClass());
        }
        putOptionalStopwatch(parentOrCurrentContext, Optional.empty(), COUNTDOWN_START_PREFIX + Thread.currentThread().getName());
        parentOrCurrentContext.getStore(NAMESPACE).remove(CLASS_LEVEL_MARKER_PREFIX + Thread.currentThread().getName());
    }

    @Override
    public void testFailed(
            final ExtensionContext context,
            final Throwable throwable) {
        final var stopwatch = optionalStopwatch(context, MISSION_START_PREFIX + Thread.currentThread().getName());
        final var rootCause = Optional.of(throwable);
        if (stopwatch.isEmpty()) {
            findClassContext(context).ifPresent(classContext -> {
                //this is very challenging to test in this way, but can happen for sure
                final var countdownStopwatch =
                        optionalStopwatch(classContext, COUNTDOWN_START_PREFIX + Thread.currentThread().getName());
                classContext.getTestClass().ifPresent(testClass -> {
                    LOGGER.trace("Logging countdown failure of class: {} due to cause: {} for launch id: {}",
                            classContext.getTestClass().map(Class::getSimpleName).orElse(null), throwable,
                            countdownStopwatch.map(StageTimeStopwatch::getLaunchId).orElse(null));
                    launchSequenceTemplate.countdownFailure(testClass, rootCause, countdownStopwatch);
                    putOptionalStopwatch(classContext, Optional.empty(), COUNTDOWN_START_PREFIX + Thread.currentThread().getName());
                    classContext.getStore(NAMESPACE).remove(CLASS_LEVEL_MARKER_PREFIX + Thread.currentThread().getName());
                });
            });
        }
        context.getTestMethod().ifPresent(method -> {
            LOGGER.trace("Logging test failure of class: {} and method: {} for launch id: {}",
                    method.getDeclaringClass().getSimpleName(), method.getName(),
                    stopwatch.map(StageTimeStopwatch::getLaunchId).orElse(null));
            launchSequenceTemplate.launchFailure(method, rootCause, stopwatch);
        });
    }

    @Override
    public void testSuccessful(final ExtensionContext context) {

        context.getTestMethod().ifPresent(method -> {
            final var stopwatch =
                    optionalStopwatch(context, MISSION_START_PREFIX + Thread.currentThread().getName());
            LOGGER.trace("Logging test success of class: {} and method: {} for launch id: {}",
                    method.getDeclaringClass().getSimpleName(), method.getName(),
                    stopwatch.map(StageTimeStopwatch::getLaunchId).orElse(null));
            launchSequenceTemplate.launchSuccess(method, stopwatch);
            putOptionalStopwatch(context, Optional.empty(), MISSION_START_PREFIX + Thread.currentThread().getName());
        });
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        findClassContext(context).ifPresent(this::markCountdownSuccessful);
        context.getTestMethod().ifPresent(method -> {
            final var displayName = getDisplayNameOfContext(context);
            final var stopwatch = launchSequenceTemplate.launchImminent(method, displayName);
            putOptionalStopwatch(context, stopwatch, MISSION_START_PREFIX + Thread.currentThread().getName());
        });
    }

    private String getDisplayNameOfContext(final ExtensionContext context) {
        return context.getParent()
                .filter(parent -> parent.getTestMethod().isPresent())
                .map(parent -> parent.getDisplayName() + " " + context.getDisplayName())
                .orElse(context.getDisplayName());
    }

    private Optional<ExtensionContext> findClassContext(final ExtensionContext context) {
        final var parent = context.getParent();
        //if the test class is not set, we went too far up the chain
        if (context.getStore(NAMESPACE).get(CLASS_LEVEL_MARKER_PREFIX + Thread.currentThread().getName()) == null) {
            return Optional.empty();
        }
        //fall back either when recursion turned up empty or when we have no parent
        return parent.map(recursion -> findClassContext(recursion).orElse(context)).or(() -> Optional.of(context));
    }

    private void markCountdownSuccessful(final ExtensionContext classContext) {
        final var parentStore = classContext.getStore(NAMESPACE);
        final var perClassMode = classContext.getTestInstanceLifecycle().filter(l -> l == PER_CLASS).isPresent();
        final var perMethodMode = classContext.getTestInstanceLifecycle().filter(l -> l == PER_METHOD).isPresent();
        final var firstRun = parentStore.get(COUNTDOWN_SUCCESS_PREFIX + Thread.currentThread().getName(), Boolean.class) == null;
        if ((perClassMode && firstRun) || perMethodMode) {
            classContext.getTestClass().ifPresent(testClass -> {
                final var stopwatch =
                        optionalStopwatch(classContext, COUNTDOWN_START_PREFIX + Thread.currentThread().getName());
                LOGGER.trace("Logging successful countdown of class: {} for launchId: {}",
                        testClass.getSimpleName(), stopwatch.map(StageTimeStopwatch::getLaunchId).orElse(null));
                launchSequenceTemplate.countdownSuccess(testClass, stopwatch);
                putOptionalStopwatch(classContext, Optional.empty(), COUNTDOWN_START_PREFIX + Thread.currentThread().getName());
            });
            if (classContext.getTestClass().isEmpty()) {
                LOGGER.warn("Test class is not found in store. Reporting is most probably incomplete.");
            }
            final var failed = classContext.getExecutionException()
                    .filter(e -> !(e instanceof TestAbortedException))
                    .isPresent();
            parentStore.put(COUNTDOWN_SUCCESS_PREFIX + Thread.currentThread().getName(), !failed);
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

    private Optional<StageTimeStopwatch> optionalStopwatch(
            final ExtensionContext context,
            final String key) {
        return Optional.ofNullable(getTestInstanceContext(context).getStore(NAMESPACE).getOrDefault(key, StageTimeStopwatch.class, null));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private void putOptionalStopwatch(
            final ExtensionContext context,
            final Optional<StageTimeStopwatch> stopwatch,
            final String key) {
        LOGGER.trace("Storing stopwatch for key: {} with launchId: {}",
                key, stopwatch.map(StageTimeStopwatch::getLaunchId).orElse(null));
        if (stopwatch.isPresent()) {
            getTestInstanceContext(context).getStore(NAMESPACE).put(key, stopwatch.get());
        } else {
            getTestInstanceContext(context).getStore(NAMESPACE).remove(key);
        }
    }

    private ExtensionContext getTestInstanceContext(final ExtensionContext context) {
        var result = context;
        if (context.getTestMethod().isPresent()) {
            final var parent = context.getParent();
            if (parent.isPresent()) {
                result = parent.get();
            }
        }
        return result;
    }
}
