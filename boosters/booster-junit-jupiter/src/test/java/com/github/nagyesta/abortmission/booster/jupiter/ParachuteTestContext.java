package com.github.nagyesta.abortmission.booster.jupiter;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import com.github.nagyesta.abortmission.core.AbortMissionCommandOps;
import com.github.nagyesta.abortmission.core.AbortMissionGlobalConfiguration;
import com.github.nagyesta.abortmission.core.annotation.LaunchSequence;
import com.github.nagyesta.abortmission.core.outline.MissionOutline;
import com.github.nagyesta.abortmission.testkit.vanilla.ParachuteDrop;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.github.nagyesta.abortmission.core.MissionControl.matcher;
import static com.github.nagyesta.abortmission.core.MissionControl.percentageBasedEvaluator;
import static com.github.nagyesta.abortmission.testkit.vanilla.ParachuteDropTestAssets.parachuteDropTestInputProvider;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings({"java:S3577", "NewClassNamingConvention"})
//we want to avoid the default names to pick up only that class which we want
@LaunchAbortArmed
@LaunchSequence(ParachuteTestContext.MissionOutlineDef.class)
class ParachuteTestContext {

    private static Stream<Arguments> parachuteIndexProvider() {
        return parachuteDropTestInputProvider().mapToObj(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("parachuteIndexProvider")
    void testParachuteShouldOpenWhenCalled(final int index) {
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
            plan.put(SHARED_CONTEXT, ops -> ops.registerHealthCheck(
                    percentageBasedEvaluator(
                            matcher().classNamePattern(".+\\.ParachuteTestContext")
                                    .build())
                            .build()));
            return plan;
        }
    }
}
