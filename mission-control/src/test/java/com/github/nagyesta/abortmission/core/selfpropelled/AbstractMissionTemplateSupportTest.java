package com.github.nagyesta.abortmission.core.selfpropelled;

import com.github.nagyesta.abortmission.core.MissionControl;
import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.TestAbortedException;

abstract class AbstractMissionTemplateSupportTest {

    protected static void abort() {
        throw new TestAbortedException();
    }

    protected MissionHealthCheckEvaluator getMissionHealthCheckEvaluator(final Class<?> component,
                                                                         final String name) {
        final var evaluators = MissionControl.matchingHealthChecks(name, component);
        final var first = evaluators.stream().findFirst();
        Assertions.assertTrue(first.isPresent());
        return first.get();
    }
}
