package com.github.nagyesta.abortmission.booster.jupiter;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import com.github.nagyesta.abortmission.core.AbortMissionCommandOps;
import com.github.nagyesta.abortmission.core.annotation.SuppressLaunchFailureReporting;
import com.github.nagyesta.abortmission.core.outline.MissionOutline;
import com.github.nagyesta.abortmission.testkit.vanilla.FuelTank;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.github.nagyesta.abortmission.testkit.vanilla.FuelTankTestAssets.*;

@SuppressLaunchFailureReporting(forExceptions = {UnsupportedOperationException.class})
@LaunchAbortArmed(CONTEXT_NAME)
public class FuelTankTestContext {

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
