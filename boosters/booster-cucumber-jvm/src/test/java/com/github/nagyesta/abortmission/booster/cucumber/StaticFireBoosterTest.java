package com.github.nagyesta.abortmission.booster.cucumber;

import com.github.nagyesta.abortmission.booster.cucumber.staticfire.StaticFireTestContext;
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
                .engine("junit-vintage")
                .selectors(selectClass(StaticFireTestContext.class))
                .execute()
                .testEvents()
                .assertStatistics(stats -> stats
                        .skipped(DISABLED_CASES)
                        .started(TOTAL_CASES)
                        .succeeded(SUCCESSFUL_CASES)
                        .aborted(ABORTED_CASES)
                        .failed(FAILED_CASES));
        MissionControl.commandOps(STATIC_FIRE).allEvaluators().forEach(evaluator -> {
            if (evaluator.getMatcher().getName().contains(CENTER_CORE)) {
                assertEquals(CENTER_CORE_NOMINAL_STATS, evaluator.getStats());
            } else {
                //As there is no countdown abort here, we need to compare the regular countdown with our mission stats
                assertEquals(SIDE_BOOSTER_NOMINAL_STATS.getReadOnlyCountdown(), evaluator.getStats().getReadOnlyMission());
            }
        });
    }

}
