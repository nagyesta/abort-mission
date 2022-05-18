package com.github.nagyesta.abortmission.strongback.h2.jupiter;

import com.github.nagyesta.abortmission.core.MissionControl;
import com.github.nagyesta.abortmission.strongback.h2.H2StrongbackController;
import com.github.nagyesta.abortmission.strongback.h2.server.H2DataSourceProvider;
import com.github.nagyesta.abortmission.strongback.h2.server.H2ServerManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineTestKit;

import javax.sql.DataSource;

import static com.github.nagyesta.abortmission.testkit.spring.StaticFireTestAssets.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class H2BackedStaticFireBoosterTest {

    static final int PORT_NUMBER = 29944;
    private static final String PASSWORD = "abort-mission";
    private static H2StrongbackController strongbackController;

    @BeforeAll
    static void beforeAll() {
        final H2ServerManager serverManager = new H2ServerManager(PASSWORD, PORT_NUMBER, true);
        final DataSource dataSource = H2DataSourceProvider.createDefaultDataSource(PORT_NUMBER);
        strongbackController = new H2StrongbackController(serverManager, dataSource);
        strongbackController.erect();
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
    public void testParallelAssumption() {
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
