package com.github.nagyesta.abortmission.core.telemetry.stats;

import com.github.nagyesta.abortmission.core.AbortMissionCommandOps;
import com.github.nagyesta.abortmission.core.telemetry.converter.LaunchTelemetryConverter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link LaunchTelemetryDataSource} using the internally collected in-memory contexts.
 */
public class DefaultLaunchTelemetryDataSource implements LaunchTelemetryDataSource {

    private final LaunchTelemetryConverter converter;
    private final Map<String, AbortMissionCommandOps> nameSpaces;

    /**
     * Constructor setting the converter and the namespaces needed for fetching the internal context telemetry.
     *
     * @param converter  The converter we are using to help to process the data.
     * @param nameSpaces The namespaces used during test execution,
     */
    public DefaultLaunchTelemetryDataSource(final LaunchTelemetryConverter converter,
                                            final Map<String, AbortMissionCommandOps> nameSpaces) {
        this.converter = Objects.requireNonNull(converter, "Converter cannot be null.");
        this.nameSpaces = Objects.requireNonNull(nameSpaces, "Namespaces cannot be null.");
    }

    /**
     * Collects the namespace information from {@link AbortMissionCommandOps} and calls
     * the constructor with the results.
     *
     * @return the collected launch telemetry data.
     */
    public static DefaultLaunchTelemetryDataSource collect() {
        return new DefaultLaunchTelemetryDataSource(
                new LaunchTelemetryConverter(), resolveContextMap());
    }

    protected static Map<String, AbortMissionCommandOps> resolveContextMap() {
        final Map<Boolean, List<String>> contextNames = AbortMissionCommandOps.contextNames().stream()
                .collect(Collectors.partitioningBy(String::isEmpty));
        final Map<String, AbortMissionCommandOps> nameSpaces = new TreeMap<>();
        contextNames.getOrDefault(false, Collections.emptyList()).forEach(s -> nameSpaces.put(s, AbortMissionCommandOps.named(s)));
        if (contextNames.containsKey(true) && !contextNames.get(true).isEmpty()) {
            nameSpaces.put("", AbortMissionCommandOps.shared());
        }
        return nameSpaces;
    }

    @Override
    public SortedMap<String, ClassTelemetry> fetchClassStatistics() {
        return converter.processClassStatistics(nameSpaces);
    }
}
