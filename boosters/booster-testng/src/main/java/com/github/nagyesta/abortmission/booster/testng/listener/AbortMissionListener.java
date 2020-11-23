package com.github.nagyesta.abortmission.booster.testng.listener;

import com.github.nagyesta.abortmission.booster.testng.annotation.LaunchAbortArmed;
import com.github.nagyesta.abortmission.core.LaunchSequenceTemplate;
import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;
import com.github.nagyesta.abortmission.core.telemetry.ReportingHelper;
import com.github.nagyesta.abortmission.core.telemetry.watch.StageTimeStopwatch;
import org.testng.*;
import org.testng.internal.ConstructorOrMethod;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;

import static com.github.nagyesta.abortmission.core.MissionControl.annotationContextEvaluator;
import static com.github.nagyesta.abortmission.core.MissionControl.matchingHealthChecks;

public class AbortMissionListener implements ITestListener, IClassListener, ISuiteListener {

    private static final ThreadLocal<Optional<StageTimeStopwatch>> STORE = new ThreadLocal<>();
    private final LaunchSequenceTemplate launchSequenceTemplate =
            new LaunchSequenceTemplate(this::doAbort, this::findEvaluators, this::findEvaluators);

    @Override
    public void onTestStart(final ITestResult result) {
        final Class<?> testInstanceClass = result.getTestClass().getRealClass();
        final Optional<Throwable> throwable = Optional.ofNullable(result.getThrowable());
        if (throwable.isPresent()) {
            // when test setup threw an exception, we are not logging success
            launchSequenceTemplate.countdownFailure(testInstanceClass, throwable, stopwatchFromStore());
        } else {
            launchSequenceTemplate.countdownSuccess(testInstanceClass, stopwatchFromStore());
            optionalMethod(result).ifPresent(method -> {
                STORE.set(launchSequenceTemplate.launchImminent(method));
            });
        }
    }

    @Override
    public void onTestSuccess(final ITestResult result) {
        optionalMethod(result).ifPresent(method -> launchSequenceTemplate.launchSuccess(method, stopwatchFromStore()));
    }

    @Override
    public void onTestFailure(final ITestResult result) {
        optionalMethod(result)
                .ifPresent(method -> launchSequenceTemplate
                        .launchFailure(method, Optional.ofNullable(result.getThrowable()), stopwatchFromStore()));
    }

    @Override
    public void onBeforeClass(final ITestClass testClass) {
        final Class<?> testInstanceClass = testClass.getRealClass();
        STORE.set(launchSequenceTemplate.launchGoNoGo(testInstanceClass));
    }

    @Override
    public void onFinish(final ISuite suite) {
        new ReportingHelper().report();
    }

    private Optional<StageTimeStopwatch> stopwatchFromStore() {
        Optional<StageTimeStopwatch> stopWatch = STORE.get();
        //noinspection OptionalAssignedToNull
        if (stopWatch == null) {
            stopWatch = Optional.empty();
        }
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
