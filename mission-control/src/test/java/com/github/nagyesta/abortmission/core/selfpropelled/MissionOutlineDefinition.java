package com.github.nagyesta.abortmission.core.selfpropelled;

import com.github.nagyesta.abortmission.core.AbortMissionCommandOps;
import com.github.nagyesta.abortmission.core.healthcheck.impl.PercentageBasedMissionHealthCheckEvaluator;
import com.github.nagyesta.abortmission.core.matcher.impl.MissionHealthCheckMatcherBuilder;
import com.github.nagyesta.abortmission.core.outline.MissionOutline;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MissionOutlineDefinition extends MissionOutline {

    static final String SELF_PROPELLED_RUNNABLE = "self-propelled-runnable-";
    static final String SELF_PROPELLED_CALLABLE = "self-propelled-callable-";

    private static void callableConfig(final AbortMissionCommandOps ops) {
        ops.registerHealthCheck(
                PercentageBasedMissionHealthCheckEvaluator
                        .builder(MissionHealthCheckMatcherBuilder.builder()
                                .classNamePattern(SimpleCallableMissionTemplateSupportTest.class.getName())
                                .build())
                        .build()
        );
        ops.registerHealthCheck(
                PercentageBasedMissionHealthCheckEvaluator
                        .builder(MissionHealthCheckMatcherBuilder.builder()
                                .classNamePattern(CallableMissionTemplateSupportTest.class.getName())
                                .build())
                        .build()
        );
    }

    private static void runnableConfig(final AbortMissionCommandOps ops) {
        ops.registerHealthCheck(
                PercentageBasedMissionHealthCheckEvaluator
                        .builder(MissionHealthCheckMatcherBuilder.builder()
                                .classNamePattern(SimpleRunnableMissionTemplateSupportTest.class.getName())
                                .build())
                        .build()
        );
        ops.registerHealthCheck(
                PercentageBasedMissionHealthCheckEvaluator
                        .builder(MissionHealthCheckMatcherBuilder.builder()
                                .classNamePattern(RunnableMissionTemplateSupportTest.class.getName())
                                .build())
                        .build()
        );
    }

    @Override
    protected Map<String, Consumer<AbortMissionCommandOps>> defineOutline() {
        final Map<String, Consumer<AbortMissionCommandOps>> map = new HashMap<>();
        map.put(SELF_PROPELLED_CALLABLE + false, MissionOutlineDefinition::callableConfig);
        map.put(SELF_PROPELLED_CALLABLE + true, MissionOutlineDefinition::callableConfig);
        map.put(SELF_PROPELLED_RUNNABLE + false, MissionOutlineDefinition::runnableConfig);
        map.put(SELF_PROPELLED_RUNNABLE + true, MissionOutlineDefinition::runnableConfig);
        return map;
    }
}
