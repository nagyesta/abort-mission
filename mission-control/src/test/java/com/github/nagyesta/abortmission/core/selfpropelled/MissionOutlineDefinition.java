package com.github.nagyesta.abortmission.core.selfpropelled;

import com.github.nagyesta.abortmission.core.AbortMissionCommandOps;
import com.github.nagyesta.abortmission.core.healthcheck.impl.PercentageBasedMissionHealthCheckEvaluator;
import com.github.nagyesta.abortmission.core.matcher.impl.MissionHealthCheckMatcherBuilder;
import com.github.nagyesta.abortmission.core.outline.MissionOutline;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MissionOutlineDefinition extends MissionOutline {

    static final String SELF_PROPELLED_RUNNABLE = "self-propelled-runnable";
    static final String SELF_PROPELLED_CALLABLE = "self-propelled-callable";

    @Override
    protected Map<String, Consumer<AbortMissionCommandOps>> defineOutline() {
        final Map<String, Consumer<AbortMissionCommandOps>> map = new HashMap<>();
        map.put(SELF_PROPELLED_RUNNABLE, ops -> ops
                .registerHealthCheck(
                        PercentageBasedMissionHealthCheckEvaluator
                                .builder(MissionHealthCheckMatcherBuilder.builder().anyClass().build())
                                .build()
                ));
        map.put(SELF_PROPELLED_CALLABLE, ops -> ops
                .registerHealthCheck(
                        PercentageBasedMissionHealthCheckEvaluator
                                .builder(MissionHealthCheckMatcherBuilder.builder().anyClass().build())
                                .build()
                ));
        return map;
    }
}
