package com.github.nagyesta.abortmission.booster.jupiter;

import com.github.nagyesta.abortmission.booster.jupiter.extractor.TagDependencyNameExtractor;
import com.github.nagyesta.abortmission.core.AbortMissionCommandOps;
import com.github.nagyesta.abortmission.core.AbortMissionGlobalConfiguration;
import com.github.nagyesta.abortmission.core.outline.MissionOutline;
import com.github.nagyesta.abortmission.testkit.spring.StaticFireTestAssets;

import java.util.Map;
import java.util.function.Consumer;

public class MissionOutlineDefinition extends MissionOutline {

    @Override
    protected Map<String, Consumer<AbortMissionCommandOps>> defineOutline() {
        return StaticFireTestAssets.getMissionPlan(new TagDependencyNameExtractor());
    }

    @Override
    protected void overrideGlobalConfig(final AbortMissionGlobalConfiguration config) {
        config.setStackTraceFilter(stackTraceElement -> stackTraceElement.getClassName().startsWith("com.github."));
    }
}
