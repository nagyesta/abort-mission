package com.github.nagyesta.abortmission.reporting.json;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.SortedSet;
import java.util.TreeSet;

@NoArgsConstructor
@Data
@SuppressWarnings({"checkstyle:DesignForExtension", "checkstyle:JavadocVariable"})
public class StageLaunchStatsJson {
    private SortedSet<String> matcherNames = new TreeSet<>();
    private SortedSet<TestRunJson> timeMeasurements = new TreeSet<>();
    private StatsJson stats;
}
