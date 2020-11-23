package com.github.nagyesta.abortmission.booster.jupiter;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import com.github.nagyesta.abortmission.core.AbortMissionCommandOps;
import com.github.nagyesta.abortmission.core.annotation.SuppressLaunchFailureReporting;
import com.github.nagyesta.abortmission.core.outline.MissionOutline;
import com.github.nagyesta.abortmission.testkit.vanilla.FuelTank;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.github.nagyesta.abortmission.testkit.vanilla.FuelTankTestAssets.*;

@SuppressLaunchFailureReporting(forExceptions = {UnsupportedOperationException.class})
@LaunchAbortArmed(PER_CLASS_CONTEXT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FuelTankTestContextPerClass {

    static {
        new MissionOutline() {
            @Override
            protected Map<String, Consumer<AbortMissionCommandOps>> defineOutline() {
                return getMissionPlan();
            }
        }.initialBriefing();
    }

    private static Stream<Arguments> fuelTankLoadAmountProvider() {
        return fuelTankTestInputProvider().mapToObj(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("fuelTankLoadAmountProvider")
    public void testFuelTankShouldFillWhenCalled(final int loadAmount) {
        //given
        final FuelTank underTest = new FuelTank();

        //when
        underTest.load(loadAmount);

        //then no exception;
    }

}
