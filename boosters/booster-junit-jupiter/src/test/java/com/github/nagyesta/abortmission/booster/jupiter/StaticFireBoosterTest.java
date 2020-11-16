package com.github.nagyesta.abortmission.booster.jupiter;

import com.github.nagyesta.abortmission.core.MissionControl;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineTestKit;

import static com.github.nagyesta.abortmission.testkit.spring.StaticFireTestAssets.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class StaticFireBoosterTest {

    @Test
    @Tag("integration")
    @SuppressWarnings("checkstyle:MagicNumber")
    public void testAssumption() throws NoSuchMethodException {
        EngineTestKit
                .engine("junit-jupiter")
                .selectors(selectClass(StaticFireTestCenterCoreOnly.class),
                        selectClass(StaticFireTestWithSideBoosters.class),
                        selectClass(StaticFireTestWithSideBoostersPerClass.class))
                .execute()
                .testEvents()
                .assertStatistics(stats -> stats
                        .skipped(DISABLED_CASES)
                        .started(TOTAL_CASES)
                        .succeeded(SUCCESSFUL_CASES)
                        .aborted(ABORTED_CASES)
                        .failed(FAILED_CASES));
        MissionControl.matchingHealthChecks(STATIC_FIRE, StaticFireTestWithSideBoosters.class)
                .forEach(evaluator -> assertEquals(SIDE_BOOSTER_NOMINAL_STATS, evaluator.getStats()));
        MissionControl.matchingHealthChecks(STATIC_FIRE, StaticFireTestWithSideBoostersPerClass.class)
                .forEach(evaluator -> assertEquals(SIDE_BOOSTER_NOMINAL_STATS_PER_CLASS, evaluator.getStats()));
        MissionControl.matchingHealthChecks(STATIC_FIRE, StaticFireTestCenterCoreOnly.class.getDeclaredMethod("testIsOnFire"))
                .forEach(evaluator -> assertEquals(CENTER_CORE_NOMINAL_STATS, evaluator.getStats()));
    }
}
