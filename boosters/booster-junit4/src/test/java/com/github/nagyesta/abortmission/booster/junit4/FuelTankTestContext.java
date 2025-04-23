package com.github.nagyesta.abortmission.booster.junit4;

import com.github.nagyesta.abortmission.booster.junit4.annotation.LaunchAbortArmed;
import com.github.nagyesta.abortmission.booster.junit4.support.LaunchAbortTestWatcher;
import com.github.nagyesta.abortmission.core.AbortMissionCommandOps;
import com.github.nagyesta.abortmission.core.annotation.SuppressLaunchFailureReporting;
import com.github.nagyesta.abortmission.core.outline.MissionOutline;
import com.github.nagyesta.abortmission.testkit.vanilla.FuelTank;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.github.nagyesta.abortmission.testkit.vanilla.FuelTankTestAssets.*;

@SuppressWarnings({"checkstyle:JavadocVariable", "checkstyle:VisibilityModifier", "java:S3577"})
//we want to avoid the default names to pick up only that class which we want
@SuppressLaunchFailureReporting(forExceptions = {UnsupportedOperationException.class})
@LaunchAbortArmed(CONTEXT_NAME)
@RunWith(Parameterized.class)
public class FuelTankTestContext {

    static {
        new MissionOutline() {
            @Override
            protected Map<String, Consumer<AbortMissionCommandOps>> defineOutline() {
                return getMissionPlan();
            }
        }.initialBriefing();
    }

    @Rule
    public TestRule watcher = new LaunchAbortTestWatcher(this.getClass());

    @Parameterized.Parameter
    public int loadAmount;

    @Parameterized.Parameters
    public static List<Integer> fuelTankLoadAmountProvider() {
        return fuelTankTestInputProvider().boxed().toList();
    }

    @SuppressWarnings("java:S2699")
    @Test
    public void testFuelTankShouldFillWhenCalled() {
        //given
        final var underTest = new FuelTank();

        //when
        underTest.load(loadAmount);

        //then no exception
    }

}
