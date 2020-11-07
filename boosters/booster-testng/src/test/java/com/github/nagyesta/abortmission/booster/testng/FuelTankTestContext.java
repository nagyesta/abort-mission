package com.github.nagyesta.abortmission.booster.testng;

import com.github.nagyesta.abortmission.booster.testng.annotation.LaunchAbortArmed;
import com.github.nagyesta.abortmission.booster.testng.listener.AbortMissionListener;
import com.github.nagyesta.abortmission.core.AbortMissionCommandOps;
import com.github.nagyesta.abortmission.core.annotation.SuppressLaunchFailureReporting;
import com.github.nagyesta.abortmission.core.outline.MissionOutline;
import com.github.nagyesta.abortmission.testkit.vanilla.FuelTank;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.function.Consumer;

import static com.github.nagyesta.abortmission.testkit.vanilla.FuelTankTestAssets.*;

@SuppressLaunchFailureReporting(forExceptions = {UnsupportedOperationException.class})
@LaunchAbortArmed(CONTEXT_NAME)
@Listeners(AbortMissionListener.class)
public class FuelTankTestContext {

    static {
        new MissionOutline() {
            @Override
            protected Map<String, Consumer<AbortMissionCommandOps>> defineOutline() {
                return getMissionPlan();
            }
        }.initialBriefing();
    }

    @DataProvider(name = "fuelTankLoadAmountProvider")
    private static Object[] fuelTankLoadAmountProvider() {
        return fuelTankTestInputProvider().boxed().toArray();
    }

    @Test(dataProvider = "fuelTankLoadAmountProvider")
    public void testFuelTankShouldFillWhenCalled(final int loadAmount) {
        //given
        final FuelTank underTest = new FuelTank();

        //when
        underTest.load(loadAmount);

        //then no exception;
    }

}
