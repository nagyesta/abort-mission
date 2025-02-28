package com.github.nagyesta.abortmission.booster.junit4;

import com.github.nagyesta.abortmission.booster.junit4.annotation.LaunchAbortArmed;
import com.github.nagyesta.abortmission.booster.junit4.support.LaunchAbortTestWatcher;
import com.github.nagyesta.abortmission.core.AbortMissionCommandOps;
import com.github.nagyesta.abortmission.core.AbortMissionGlobalConfiguration;
import com.github.nagyesta.abortmission.core.annotation.LaunchSequence;
import com.github.nagyesta.abortmission.core.outline.MissionOutline;
import com.github.nagyesta.abortmission.testkit.vanilla.ParachuteDrop;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.github.nagyesta.abortmission.core.MissionControl.matcher;
import static com.github.nagyesta.abortmission.core.MissionControl.percentageBasedEvaluator;
import static com.github.nagyesta.abortmission.testkit.vanilla.ParachuteDropTestAssets.parachuteDropTestInputProvider;
import static org.junit.Assert.assertTrue;

@SuppressWarnings({"checkstyle:JavadocVariable", "checkstyle:VisibilityModifier"})
@LaunchAbortArmed
@LaunchSequence(ParachuteTestContext.MissionOutlineDef.class)
@RunWith(Parameterized.class)
public class ParachuteTestContext {

    @Rule
    public LaunchAbortTestWatcher watcher = new LaunchAbortTestWatcher(this.getClass());

    @Parameterized.Parameter
    public int index;

    @Parameterized.Parameters
    public static List<Integer> parachuteIndexProvider() {
        return parachuteDropTestInputProvider().boxed().collect(Collectors.toList());
    }

    @Test
    public void testParachuteShouldOpenWhenCalled() {
        //given
        final var underTest = new ParachuteDrop();

        //when
        final var actual = underTest.canOpenParachute(index);

        //then
        assertTrue("Parachutes should open.", actual);
    }

    public static class MissionOutlineDef extends MissionOutline {

        @Override
        protected void overrideGlobalConfig(final AbortMissionGlobalConfiguration config) {
            config.setStackTraceFilter(stackTraceElement -> stackTraceElement.getClassName().startsWith("com.github."));
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
