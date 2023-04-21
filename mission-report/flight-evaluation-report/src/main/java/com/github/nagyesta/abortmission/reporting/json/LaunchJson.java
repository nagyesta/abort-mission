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

    public boolean isFailure() {
        return classes.values().stream()
                .anyMatch(c -> c.streamAllTimeMeasurements().anyMatch(TestRunJson::isFailure));
    }
}
