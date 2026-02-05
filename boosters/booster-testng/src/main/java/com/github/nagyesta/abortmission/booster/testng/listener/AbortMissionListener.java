package com.github.nagyesta.abortmission.booster.testng.listener;

import com.github.nagyesta.abortmission.booster.testng.annotation.LaunchAbortArmed;
import com.github.nagyesta.abortmission.core.LaunchSequenceTemplate;
import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;
import com.github.nagyesta.abortmission.core.telemetry.ReportingHelper;
import com.github.nagyesta.abortmission.core.telemetry.watch.StageTimeStopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.*;
import org.testng.internal.ConstructorOrMethod;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.nagyesta.abortmission.core.MissionControl.annotationContextEvaluator;
import static com.github.nagyesta.abortmission.core.MissionControl.matchingHealthChecks;

public class AbortMissionListener implements ITestListener, IClassListener, ISuiteListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbortMissionListener.class);
    private static final ThreadLocal<Optional<StageTimeStopwatch>> STORE = new ThreadLocal<>();
    private final LaunchSequenceTemplate launchSequenceTemplate =
            new LaunchSequenceTemplate(this::doAbort, this::findEvaluators, this::findEvaluators);

    @Override
    public void onTestStart(final ITestResult result) {
        final var testInstanceClass = result.getTestClass().getRealClass();
        final var throwable = Optional.ofNullable(result.getThrowable());
        final var stopwatch = stopwatchFromStore();
        if (throwable.isPresent()) {
            LOGGER.trace("Countdown failed for class: {} with launch id: {}", testInstanceClass.getSimpleName(),
                    stopwatch.map(StageTimeStopwatch::getLaunchId).orElse(null));
            // when test setup threw an exception, we are not logging success
            launchSequenceTemplate.countdownFailure(testInstanceClass, throwable, stopwatch);
        } else {
            LOGGER.trace("Countdown completed for class: {} with launch id: {}", testInstanceClass.getSimpleName(),
                    stopwatch.map(StageTimeStopwatch::getLaunchId).orElse(null));
            launchSequenceTemplate.countdownSuccess(testInstanceClass, stopwatch);
            optionalMethod(result).ifPresent(method -> {
                final var displayName = resolveDisplayName(result, method);
                STORE.set(launchSequenceTemplate.launchImminent(method, displayName));
            });
        }
    }

    private String resolveDisplayName(
            final ITestResult result,
            final Method method) {
        return method.getName() + Optional.ofNullable(result.getParameters())
                .filter(p -> p.length > 0)
                .map(p -> Arrays.stream(p).map(Object::toString).collect(Collectors.joining(",")))
                .map(s -> " " + s)
                .orElse("");
    }

    @Override
    public void onTestSuccess(final ITestResult result) {
        optionalMethod(result).ifPresent(method -> {
            final var stopwatch = stopwatchFromStore();
            LOGGER.trace("Log mission success for class: {} and method: {} with launch id: {}",
                    method.getDeclaringClass().getSimpleName(), method.getName(),
                    stopwatch.map(StageTimeStopwatch::getLaunchId).orElse(null));
            launchSequenceTemplate.launchSuccess(method, stopwatch);
        });
    }

    @Override
    public void onTestFailure(final ITestResult result) {
        optionalMethod(result)
                .ifPresent(method -> {
                    final var stopwatch = stopwatchFromStore();
                    LOGGER.trace("Log mission failure for class: {} and method: {} with launch id: {}",
                            method.getDeclaringClass().getSimpleName(), method.getName(),
                            stopwatch.map(StageTimeStopwatch::getLaunchId).orElse(null));
                    launchSequenceTemplate.launchFailure(method, Optional.ofNullable(result.getThrowable()), stopwatch);
                });
    }

    @Override
    public void onBeforeClass(final ITestClass testClass) {
        final var testInstanceClass = testClass.getRealClass();
        LOGGER.trace("Preparing test instance for class: {}", testInstanceClass.getSimpleName());
        STORE.set(launchSequenceTemplate.launchGoNoGo(testInstanceClass, testInstanceClass.getSimpleName()));
    }

    @Override
    public void onFinish(final ISuite suite) {
        new ReportingHelper().report();
    }

    private Optional<StageTimeStopwatch> stopwatchFromStore() {
        final var stopWatch = Optional.ofNullable(STORE.get()).flatMap(Function.identity());
        STORE.remove();
        return stopWatch;
    }

    private Optional<Method> optionalMethod(final ITestResult result) {
        return Optional.ofNullable(result.getMethod())
                .map(ITestNGMethod::getConstructorOrMethod)
                .map(ConstructorOrMethod::getMethod);
    }

    private void doAbort() {
        throw new SkipException("Aborting test as mission checks indicate failure.");
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
}
