package com.github.nagyesta.abortmission.reporting.json;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Stream;

@NoArgsConstructor
@Data
@SuppressWarnings({"checkstyle:DesignForExtension", "checkstyle:JavadocVariable"})
public class ClassJson {
    private String className;
    private StageLaunchStatsJson countdown;
    private Map<String, StageLaunchStatsJson> launches = new TreeMap<>();

    public Stream<TestRunJson> streamAllTimeMeasurements() {
        final Stream<TestRunJson> countdownStream = Optional.ofNullable(countdown)
                .map(StageLaunchStatsJson::getTimeMeasurements)
                .stream().flatMap(Collection::stream);
        final Stream<TestRunJson> launchStream = Optional.ofNullable(launches)
                .map(Map::values)
                .stream().flatMap(Collection::stream)
                .map(StageLaunchStatsJson::getTimeMeasurements)
                .flatMap(SortedSet::stream);
        return Stream.concat(countdownStream, launchStream).sorted();
    }
}
