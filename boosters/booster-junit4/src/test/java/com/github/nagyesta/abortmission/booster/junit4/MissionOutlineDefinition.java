package com.github.nagyesta.abortmission.booster.junit4;

import com.github.nagyesta.abortmission.booster.junit4.extractor.CategoryDependencyNameExtractor;
import com.github.nagyesta.abortmission.core.AbortMissionCommandOps;
import com.github.nagyesta.abortmission.core.outline.MissionOutline;
import com.github.nagyesta.abortmission.testkit.spring.StaticFireTestAssets;

import java.util.Map;
import java.util.function.Consumer;

public class MissionOutlineDefinition extends MissionOutline {

    @Override
    protected Map<String, Consumer<AbortMissionCommandOps>> defineOutline() {
        return StaticFireTestAssets.getMissionPlan(new CategoryDependencyNameExtractor());
    }
}
