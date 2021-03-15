package com.github.nagyesta.abortmission.strongback.rmi.jupiter;

import com.github.nagyesta.abortmission.booster.jupiter.extractor.TagDependencyNameExtractor;
import com.github.nagyesta.abortmission.core.AbortMissionCommandOps;
import com.github.nagyesta.abortmission.core.extractor.DependencyNameExtractor;
import com.github.nagyesta.abortmission.core.healthcheck.StageStatisticsCollectorFactory;
import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;
import com.github.nagyesta.abortmission.core.outline.MissionOutline;
import com.github.nagyesta.abortmission.strongback.rmi.server.RmiServerConstants;
import com.github.nagyesta.abortmission.strongback.rmi.server.RmiServiceProvider;
import com.github.nagyesta.abortmission.strongback.rmi.stats.RmiBackedStageStatisticsCollectorFactory;

import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.github.nagyesta.abortmission.core.MissionControl.*;
import static com.github.nagyesta.abortmission.testkit.spring.StaticFireTestAssets.*;

public class MissionOutlineDefinition extends MissionOutline {

    @Override
    @SuppressWarnings("checkstyle:MagicNumber")
    protected Map<String, Consumer<AbortMissionCommandOps>> defineOutline() {
        final DependencyNameExtractor extractor = new TagDependencyNameExtractor();
        final Registry registry = RmiServiceProvider.lookupRegistry(RmiServerConstants.DEFAULT_RMI_PORT);
        final Map<String, Consumer<AbortMissionCommandOps>> plan = new HashMap<>();
        plan.put(STATIC_FIRE, ops -> {
            final StageStatisticsCollectorFactory factory = getCollectorFactory(STATIC_FIRE, registry);
            final MissionHealthCheckMatcher sideBoosterMatcher = matcher()
                    .dependencyWith(SIDE_BOOSTER)
                    .extractor(extractor).build();
            final MissionHealthCheckMatcher centerCoreMatcher = matcher()
                    .dependencyWith(CENTER_CORE)
                    .extractor(extractor).build();
            ops.registerHealthCheck(
                    percentageBasedEvaluator(sideBoosterMatcher, factory)
                            .abortThreshold(10)
                            .burnInTestCount(2).build());
            ops.registerHealthCheck(
                    percentageBasedEvaluator(centerCoreMatcher, factory)
                            .build());
        });
        plan.put(PARALLEL, ops -> {
            final StageStatisticsCollectorFactory factory = getCollectorFactory(PARALLEL, registry);
            final MissionHealthCheckMatcher anyClassMatcher = matcher().anyClass().build();
            ops.registerHealthCheck(
                    reportOnlyEvaluator(anyClassMatcher, factory)
                            .build());
        });
        return plan;
    }

    private StageStatisticsCollectorFactory getCollectorFactory(final String contextName, final Registry registry) {
        return new RmiBackedStageStatisticsCollectorFactory(contextName, registry);
    }

}
