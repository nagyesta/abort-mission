package com.github.nagyesta.abortmission.core.outline;

import com.github.nagyesta.abortmission.core.AbortMissionCommandOps;
import com.github.nagyesta.abortmission.core.MissionControl;
import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;
import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

class MissionOutlineTest {

    private static final String EMPTY = "";
    private static final String NAME = "name";
    private static final MissionHealthCheckMatcher MATCHER = MissionControl.matcher().anyClass().build();
    private static final MissionHealthCheckEvaluator EVALUATOR = MissionControl.percentageBasedEvaluator(MATCHER).build();

    @ParameterizedTest
    @ValueSource(strings = {EMPTY, NAME})
    void testInitialBriefingShouldAvoidRegisteringTwiceWhenCalledWithTheSameName(final String name) {
        //given
        final MissionOutline firstOutline = new MissionOutline() {
            @Override
            protected Map<String, Consumer<AbortMissionCommandOps>> defineOutline() {
                return Collections.singletonMap(name, ops -> ops.registerHealthCheck(EVALUATOR));
            }
        };
        final MissionOutline secondOutline = new MissionOutline() {
            @Override
            protected Map<String, Consumer<AbortMissionCommandOps>> defineOutline() {
                return Collections.singletonMap(name, ops -> Assertions.fail("Should have never called."));
            }
        };

        //when
        firstOutline.initialBriefing();
        secondOutline.initialBriefing();

        //then
        final AbortMissionCommandOps actual;
        if (EMPTY.equals(name)) {
            actual = MissionControl.commandOps();
            Assertions.assertNotNull(actual);
        } else {
            actual = MissionControl.commandOps(name);
            Assertions.assertNotNull(actual);
        }
        Assertions.assertIterableEquals(Collections.singleton(EVALUATOR), actual.allEvaluators());
    }
}
