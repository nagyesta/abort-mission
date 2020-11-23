package com.github.nagyesta.abortmission.core.selfpropelled;

import com.github.nagyesta.abortmission.core.MissionControl;
import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.TestAbortedException;

import java.util.Optional;
import java.util.Set;

abstract class AbstractMissionTemplateSupportTest {

    protected static void abort() {
        throw new TestAbortedException();
    }

    protected MissionHealthCheckEvaluator getMissionHealthCheckEvaluator(final Class<?> component,
                                                                         final String name) {
        final Set<MissionHealthCheckEvaluator> evaluators = MissionControl.matchingHealthChecks(name, component);
        final Optional<MissionHealthCheckEvaluator> first = evaluators.stream().findFirst();
        Assertions.assertTrue(first.isPresent());
        return first.get();
    }
}
