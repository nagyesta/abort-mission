package com.github.nagyesta.abortmission.booster.jupiter;

import com.github.nagyesta.abortmission.core.MissionControl;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineTestKit;

import static com.github.nagyesta.abortmission.testkit.vanilla.FuelTankTestAssets.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class FuelTankTest {

    @Test
    @Tag("integration")
    @SuppressWarnings("checkstyle:MagicNumber")
    public void testAssumption() {
        EngineTestKit
                .engine("junit-jupiter")
                .selectors(selectClass(FuelTankTestContext.class))
                .execute()
                .testEvents()
                .assertStatistics(stats -> stats
                        .skipped(DISABLED_CASES)
                        .started(TOTAL_CASES)
                        .succeeded(SUCCESSFUL_CASES)
                        .aborted(ABORTED_CASES)
                        .failed(FAILED_CASES));
        MissionControl.matchingHealthChecks(CONTEXT_NAME, FuelTankTestContext.class)
                .forEach(evaluator -> {
                    assertEquals(FUEL_TANK_NOMINAL_STATS.getReadOnlyCountdown().getSnapshot(),
                            evaluator.getStats().getReadOnlyCountdown().getSnapshot());
                    assertEquals(FUEL_TANK_NOMINAL_STATS.getReadOnlyMission().getSnapshot(),
                            evaluator.getStats().getReadOnlyMission().getSnapshot());
                });
    }

    @Test
    @Tag("integration")
    @SuppressWarnings("checkstyle:MagicNumber")
    public void testAssumptionPerClass() {
        EngineTestKit
                .engine("junit-jupiter")
                .selectors(selectClass(FuelTankTestContextPerClass.class))
                .execute()
                .testEvents()
                .assertStatistics(stats -> stats
                        .skipped(DISABLED_CASES)
                        .started(TOTAL_CASES)
                        .succeeded(SUCCESSFUL_CASES)
                        .aborted(ABORTED_CASES)
                        .failed(FAILED_CASES));
        MissionControl.matchingHealthChecks(PER_CLASS_CONTEXT, FuelTankTestContextPerClass.class)
                .forEach(evaluator -> {
                    assertEquals(FUEL_TANK_NOMINAL_STATS_PER_CLASS.getReadOnlyCountdown().getSnapshot(),
                            evaluator.getStats().getReadOnlyCountdown().getSnapshot());
                    assertEquals(FUEL_TANK_NOMINAL_STATS_PER_CLASS.getReadOnlyMission().getSnapshot(),
                            evaluator.getStats().getReadOnlyMission().getSnapshot());
                });
    }

}
