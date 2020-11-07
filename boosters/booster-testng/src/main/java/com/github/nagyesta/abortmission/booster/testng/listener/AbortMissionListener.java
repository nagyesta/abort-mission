package com.github.nagyesta.abortmission.booster.testng.listener;

import com.github.nagyesta.abortmission.booster.testng.annotation.LaunchAbortArmed;
import com.github.nagyesta.abortmission.core.LaunchSequenceTemplate;
import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;
import org.testng.*;
import org.testng.internal.ConstructorOrMethod;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.nagyesta.abortmission.core.MissionControl.annotationContextEvaluator;
import static com.github.nagyesta.abortmission.core.MissionControl.matchingHealthChecks;

public class AbortMissionListener implements ITestListener, IClassListener {

    private final LaunchSequenceTemplate launchSequenceTemplate =
            new LaunchSequenceTemplate(this::doAbort, this::findEvaluators, this::findEvaluators);

    @Override
    public void onTestStart(final ITestResult result) {
        final Class<?> testInstanceClass = result.getTestClass().getRealClass();
        final Optional<Throwable> throwable = Optional.ofNullable(result.getThrowable());
        if (throwable.isPresent()) {
            // when test setup threw an exception, we are not logging success
            logCountdownAbort(testInstanceClass);
        } else {
            optionalMethod(result).ifPresent(launchSequenceTemplate::launchImminent);
        }
        //log countdown started retroactively
        findEvaluators(testInstanceClass).forEach(MissionHealthCheckEvaluator::logCountdownStarted);
    }

    @Override
    public void onTestSuccess(final ITestResult result) {
        optionalMethod(result).ifPresent(launchSequenceTemplate::launchSuccess);
    }

    @Override
    public void onTestFailure(final ITestResult result) {
        optionalMethod(result)
                .ifPresent(method -> launchSequenceTemplate.launchFailure(method, Optional.ofNullable(result.getThrowable())));
    }

    @Override
    public void onTestFailedWithTimeout(final ITestResult result) {
        optionalMethod(result)
                .ifPresent(method -> launchSequenceTemplate.launchFailure(method, Optional.ofNullable(result.getThrowable())));
    }

    @Override
    public void onBeforeClass(final ITestClass testClass) {
        final Class<?> testInstanceClass = testClass.getRealClass();
        annotationContextEvaluator().findAndApplyLaunchPlanDefinition(testInstanceClass);

        final Set<MissionHealthCheckEvaluator> evaluators = findEvaluators(testInstanceClass);
        if (!annotationContextEvaluator().isAbortSuppressed(testInstanceClass)) {
            final Set<MissionHealthCheckEvaluator> shouldAbort = evaluators.stream()
                    .filter(MissionHealthCheckEvaluator::shouldAbort)
                    .collect(Collectors.toSet());
            shouldAbort.forEach(MissionHealthCheckEvaluator::logCountdownAborted);
            if (!shouldAbort.isEmpty()) {
                doAbort();
            }
        }
    }

    private Optional<Method> optionalMethod(final ITestResult result) {
        return Optional.ofNullable(result.getMethod())
                .map(ITestNGMethod::getConstructorOrMethod)
                .map(ConstructorOrMethod::getMethod);
    }

    /**
     * Logs pre-launch failure.
     *
     * @param testInstanceClass The test class.
     */
    private void logCountdownAbort(final Class<?> testInstanceClass) {
        // at this point TestNG already decided that the test class post processing failed and will skip all tests
        final Set<MissionHealthCheckEvaluator> evaluators = findEvaluators(testInstanceClass);
        if (!annotationContextEvaluator().isAbortSuppressed(testInstanceClass)) {
            evaluators.forEach(evaluator -> {
                if (evaluator.shouldAbortCountdown()) {
                    evaluator.logCountdownAborted();
                }
            });
        }
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
