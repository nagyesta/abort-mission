package com.github.nagyesta.abortmission.strongback.rmi.jupiter;

import com.github.nagyesta.abortmission.core.MissionControl;
import com.github.nagyesta.abortmission.strongback.rmi.RmiStrongbackController;
import com.github.nagyesta.abortmission.strongback.rmi.server.RmiServerManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineTestKit;

import static com.github.nagyesta.abortmission.testkit.spring.StaticFireTestAssets.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class RmiBackedStaticFireBoosterTest {

    private static RmiStrongbackController strongbackController;

    @BeforeAll
    static void beforeAll() {
        final RmiServerManager serverManager = new RmiServerManager();
        strongbackController = new RmiStrongbackController(serverManager);
        new Thread(() -> strongbackController.erect()).start();
    }

    @AfterAll
    static void afterAll() {
        strongbackController.retract();
    }

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
                .forEach(evaluator -> {
                    assertEquals(SIDE_BOOSTER_NOMINAL_STATS.getReadOnlyCountdown().getSnapshot(),
                            evaluator.getStats().getReadOnlyCountdown().getSnapshot());
                    assertEquals(SIDE_BOOSTER_NOMINAL_STATS.getReadOnlyMission().getSnapshot(),
                            evaluator.getStats().getReadOnlyMission().getSnapshot());
                });
        MissionControl.matchingHealthChecks(STATIC_FIRE, StaticFireTestWithSideBoostersPerClass.class)
                .forEach(evaluator -> {
                    assertEquals(SIDE_BOOSTER_NOMINAL_STATS_PER_CLASS.getReadOnlyCountdown().getSnapshot(),
                            evaluator.getStats().getReadOnlyCountdown().getSnapshot());
                    assertEquals(SIDE_BOOSTER_NOMINAL_STATS_PER_CLASS.getReadOnlyMission().getSnapshot(),
                            evaluator.getStats().getReadOnlyMission().getSnapshot());
                });
        MissionControl.matchingHealthChecks(STATIC_FIRE, StaticFireTestCenterCoreOnly.class.getDeclaredMethod("testIsOnFire"))
                .forEach(evaluator -> {
                    assertEquals(CENTER_CORE_NOMINAL_STATS.getReadOnlyCountdown().getSnapshot(),
                            evaluator.getStats().getReadOnlyCountdown().getSnapshot());
                    assertEquals(CENTER_CORE_NOMINAL_STATS.getReadOnlyMission().getSnapshot(),
                            evaluator.getStats().getReadOnlyMission().getSnapshot());
                });
    }

    @Test
    @Tag("integration")
    @SuppressWarnings("checkstyle:MagicNumber")
    public void testParallelAssumption() throws NoSuchMethodException {
        EngineTestKit
                .engine("junit-jupiter")
                .selectors(selectClass(ParallelStaticFireTestWithSideBoostersPerClassTest.class),
                        selectClass(ParallelStaticFireTestWithSideBoostersTest.class))
                .configurationParameter("junit.jupiter.execution.parallel.enabled", "true")
                .configurationParameter("junit.jupiter.execution.parallel.mode.default", "concurrent")
                .configurationParameter("junit.jupiter.execution.parallel.mode.classes.default", "concurrent")
                .configurationParameter("junit.jupiter.execution.parallel.config.strategy", "fixed")
                .configurationParameter("junit.jupiter.execution.parallel.config.fixed.parallelism", "4")
                .execute()
                .testEvents()
                .assertStatistics(stats -> stats
                        .skipped(0)
                        .started(SUCCESSFUL_PARALLEL_CASES)
                        .succeeded(SUCCESSFUL_PARALLEL_CASES)
                        .aborted(0)
                        .failed(0));
        MissionControl.matchingHealthChecks(PARALLEL, StaticFireTestWithSideBoosters.class)
                .forEach(evaluator -> {
                    assertEquals(PARALLEL_NOMINAL_STATS.getReadOnlyCountdown().getSnapshot(),
                            evaluator.getStats().getReadOnlyCountdown().getSnapshot());
                    assertEquals(PARALLEL_NOMINAL_STATS.getReadOnlyMission().getSnapshot(),
                            evaluator.getStats().getReadOnlyMission().getSnapshot());
                });
        assertTrue(ParallelStaticFireTestWithSideBoostersTest.THREADS_USED.size() > 1);
    }

}
