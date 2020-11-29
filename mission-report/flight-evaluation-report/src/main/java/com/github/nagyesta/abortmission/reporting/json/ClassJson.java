package com.github.nagyesta.abortmission.reporting.json;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.TreeMap;

@NoArgsConstructor
@Data
@SuppressWarnings({"checkstyle:DesignForExtension", "checkstyle:JavadocVariable"})
public class ClassJson {
    private String className;
    private StageLaunchStatsJson countdown;
    private StatsJson stats;
    private Map<String, StageLaunchStatsJson> launches = new TreeMap<>();
}
