package com.github.nagyesta.abortmission.reporting.json;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.TreeMap;

@NoArgsConstructor
@Data
@SuppressWarnings({"checkstyle:DesignForExtension", "checkstyle:JavadocVariable"})
public class LaunchJson {
    private Map<String, ClassJson> classes = new TreeMap<>();
    private StatsJson stats;
    private StatsJson countdownStats;
    private StatsJson missionStats;
}
