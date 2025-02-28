package com.github.nagyesta.abortmission.booster.jupiter;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import com.github.nagyesta.abortmission.core.AbortMissionCommandOps;
import com.github.nagyesta.abortmission.core.AbortMissionGlobalConfiguration;
import com.github.nagyesta.abortmission.core.annotation.LaunchSequence;
import com.github.nagyesta.abortmission.core.outline.MissionOutline;
import com.github.nagyesta.abortmission.testkit.vanilla.ParachuteDrop;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.github.nagyesta.abortmission.core.MissionControl.matcher;
import static com.github.nagyesta.abortmission.core.MissionControl.percentageBasedEvaluator;
import static com.github.nagyesta.abortmission.testkit.vanilla.ParachuteDropTestAssets.PER_CLASS_CONTEXT;
import static com.github.nagyesta.abortmission.testkit.vanilla.ParachuteDropTestAssets.parachuteDropTestInputProvider;
import static org.junit.jupiter.api.Assertions.assertTrue;

@LaunchAbortArmed(PER_CLASS_CONTEXT)
@LaunchSequence(ParachuteTestContextPerClass.MissionOutlineDef.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ParachuteTestContextPerClass {

    private static Stream<Arguments> parachuteIndexProvider() {
        return parachuteDropTestInputProvider().mapToObj(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("parachuteIndexProvider")
    public void testParachuteShouldOpenWhenCalled(final int index) {
        //given
        final var underTest = new ParachuteDrop();

        //when
        final var actual = underTest.canOpenParachute(index);

        //then
        assertTrue(actual, "Parachutes should open.");
    }

    public static class MissionOutlineDef extends MissionOutline {

        @Override
        protected void overrideGlobalConfig(final AbortMissionGlobalConfiguration config) {
            config.setStackTraceFilter(e -> e.getClassName().startsWith("com.github."));
        }

        @Override
        protected Map<String, Consumer<AbortMissionCommandOps>> defineOutline() {
            final var plan = new HashMap<String, Consumer<AbortMissionCommandOps>>();
            plan.put(PER_CLASS_CONTEXT, ops -> ops.registerHealthCheck(
                    percentageBasedEvaluator(
                            matcher().classNamePattern(".+\\.ParachuteTestContextPerClass")
                                    .build())
                            .build()));
            return plan;
        }
    }
}
