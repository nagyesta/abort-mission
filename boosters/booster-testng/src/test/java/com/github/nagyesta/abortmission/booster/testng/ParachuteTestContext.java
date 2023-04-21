package com.github.nagyesta.abortmission.booster.testng;

import com.github.nagyesta.abortmission.booster.testng.annotation.LaunchAbortArmed;
import com.github.nagyesta.abortmission.booster.testng.listener.AbortMissionListener;
import com.github.nagyesta.abortmission.core.AbortMissionCommandOps;
import com.github.nagyesta.abortmission.core.AbortMissionGlobalConfiguration;
import com.github.nagyesta.abortmission.core.annotation.LaunchSequence;
import com.github.nagyesta.abortmission.core.outline.MissionOutline;
import com.github.nagyesta.abortmission.testkit.vanilla.ParachuteDrop;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.github.nagyesta.abortmission.core.MissionControl.matcher;
import static com.github.nagyesta.abortmission.core.MissionControl.percentageBasedEvaluator;
import static com.github.nagyesta.abortmission.testkit.vanilla.ParachuteDropTestAssets.parachuteDropTestInputProvider;
import static org.testng.Assert.assertTrue;

@LaunchAbortArmed
@Listeners(AbortMissionListener.class)
@LaunchSequence(ParachuteTestContext.MissionOutlineDef.class)
public class ParachuteTestContext {

    @DataProvider(name = "parachuteIndexProvider")
    private static Object[][] parachuteIndexProvider() {
        return parachuteDropTestInputProvider().boxed()
                .map(List::of)
                .map(List::toArray)
                .collect(Collectors.toList()).toArray(new Object[(int) parachuteDropTestInputProvider().count()][1]);
    }

    @Test(dataProvider = "parachuteIndexProvider")
    public void testParachuteShouldOpenWhenCalled(final int index) {
        //given
        final ParachuteDrop underTest = new ParachuteDrop();

        //when
        final boolean actual = underTest.canOpenParachute(index);

        //then
        assertTrue(actual, "Parachutes should open.");
    }

    public static class MissionOutlineDef extends MissionOutline {

        @Override
        protected void overrideGlobalConfig(final AbortMissionGlobalConfiguration config) {
            config.setStackTraceFilter(stackTraceElement -> stackTraceElement.getClassName().startsWith("com.github."));
        }

        @Override
        protected Map<String, Consumer<AbortMissionCommandOps>> defineOutline() {
            final HashMap<String, Consumer<AbortMissionCommandOps>> plan = new HashMap<>();
            plan.put(SHARED_CONTEXT, ops -> ops.registerHealthCheck(
                    percentageBasedEvaluator(
                            matcher().classNamePattern(".+\\.ParachuteTestContext")
                                    .build())
                            .build()));
            return plan;
        }
    }
}
