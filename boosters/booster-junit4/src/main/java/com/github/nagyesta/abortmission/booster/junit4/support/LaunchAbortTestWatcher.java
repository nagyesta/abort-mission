package com.github.nagyesta.abortmission.booster.junit4.support;

import com.github.nagyesta.abortmission.booster.junit4.annotation.LaunchAbortArmed;
import com.github.nagyesta.abortmission.core.LaunchSequenceTemplate;
import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;
import org.junit.AssumptionViolatedException;
import org.junit.Before;
import org.junit.internal.runners.statements.RunAfters;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.github.nagyesta.abortmission.core.MissionControl.annotationContextEvaluator;
import static com.github.nagyesta.abortmission.core.MissionControl.matchingHealthChecks;

public class LaunchAbortTestWatcher extends TestWatcher {

    private final LaunchSequenceTemplate launchSequenceTemplate;
    private final Class<?> testClass;
    private final boolean noBefores;

    public LaunchAbortTestWatcher(final Class<?> testClass) {
        this.testClass = Objects.requireNonNull(testClass, "Test class cannot be null.");
        this.launchSequenceTemplate = new LaunchSequenceTemplate(this::doAbort, this::findEvaluators, this::findEvaluators);
        annotationContextEvaluator().findAndApplyLaunchPlanDefinition(testClass);
        noBefores = Arrays.stream(testClass.getDeclaredMethods()).noneMatch(m -> m.isAnnotationPresent(Before.class));
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        final Statement wrapped;
        if (base instanceof RunBefores || base instanceof RunAfters) {
            wrapped = wrapPreparationCall(base);
        } else {
            wrapped = wrapTestMethodCall(base, description);
        }
        return super.apply(wrapped, description);
    }

    @Override
    protected void succeeded(final Description description) {
        findMethodByDescription(description).ifPresent(launchSequenceTemplate::launchSuccess);
        super.succeeded(description);
    }

    @Override
    protected void failed(final Throwable e, final Description description) {
        findMethodByDescription(description).ifPresent(method -> launchSequenceTemplate.launchFailure(method, Optional.of(e)));
        super.failed(e, description);
    }

    private Statement wrapPreparationCall(final Statement base) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                launchSequenceTemplate.launchGoNoGo(testClass);
                base.evaluate();
            }
        };
    }

    private Statement wrapTestMethodCall(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                if (noBefores) {
                    findEvaluators(testClass).forEach(MissionHealthCheckEvaluator::logCountdownStarted);
                }
                findMethodByDescription(description).ifPresent(launchSequenceTemplate::launchImminent);
                base.evaluate();
            }
        };
    }

    private Optional<Method> findMethodByDescription(final Description description) {
        return Optional.ofNullable(description)
                .map(Description::getMethodName)
                .map(name -> name.replaceFirst("\\[[0-9]+]", ""))
                .flatMap(name -> Arrays.stream(testClass.getMethods()).filter(m -> name.equals(m.getName())).findFirst());
    }

    private Set<MissionHealthCheckEvaluator> findEvaluators(final Method method) {
        return annotationContextEvaluator().findContextName(method, LaunchAbortArmed.class, LaunchAbortArmed::value)
                .map(name -> matchingHealthChecks(name, method))
                .orElseGet(() -> matchingHealthChecks(method));
    }

    private Set<MissionHealthCheckEvaluator> findEvaluators(final Class<?> testInstanceClass) {
        return annotationContextEvaluator().findContextName(testInstanceClass, LaunchAbortArmed.class, LaunchAbortArmed::value)
                .map(name -> matchingHealthChecks(name, testInstanceClass))
                .orElseGet(() -> matchingHealthChecks(testInstanceClass));
    }

    private void doAbort() {
        throw new AssumptionViolatedException("Aborting test as mission checks indicate failure.");
    }
}
