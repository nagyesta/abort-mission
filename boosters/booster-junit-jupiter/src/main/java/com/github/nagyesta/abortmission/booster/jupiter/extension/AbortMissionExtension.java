package com.github.nagyesta.abortmission.booster.jupiter.extension;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import com.github.nagyesta.abortmission.core.LaunchSequenceTemplate;
import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.jupiter.api.extension.TestWatcher;
import org.opentest4j.TestAbortedException;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;

import static com.github.nagyesta.abortmission.core.MissionControl.annotationContextEvaluator;
import static com.github.nagyesta.abortmission.core.MissionControl.matchingHealthChecks;

public class AbortMissionExtension implements TestInstancePostProcessor, TestWatcher, BeforeEachCallback {

    private final LaunchSequenceTemplate launchSequenceTemplate =
            new LaunchSequenceTemplate(this::doAbort, this::findEvaluators, this::findEvaluators);

    @SuppressWarnings("RedundantThrows")
    @Override
    public void postProcessTestInstance(final Object testInstance, final ExtensionContext context) throws Exception {
        Optional.ofNullable(testInstance).map(Object::getClass).ifPresent(launchSequenceTemplate::launchGoNoGo);
    }

    @Override
    public void testFailed(final ExtensionContext context, final Throwable throwable) {
        context.getTestMethod().ifPresent(method -> launchSequenceTemplate.launchFailure(method, Optional.of(throwable)));
    }

    @Override
    public void testSuccessful(final ExtensionContext context) {
        context.getTestMethod().ifPresent(launchSequenceTemplate::launchSuccess);
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        context.getTestMethod().ifPresent(launchSequenceTemplate::launchImminent);
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
}
