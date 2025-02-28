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

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
    private static Object[][] fuelTankLoadAmountProvider() {
        return fuelTankTestInputProvider().boxed()
                .map(List::of)
                .map(List::toArray)
                .collect(Collectors.toList()).toArray(new Object[(int) fuelTankTestInputProvider().count()][1]);
    }

    @Test(dataProvider = "fuelTankLoadAmountProvider")
    public void testFuelTankShouldFillWhenCalled(final int loadAmount) {
        //given
        final var underTest = new FuelTank();

        //when
        underTest.load(loadAmount);

        //then no exception;
    }

}
