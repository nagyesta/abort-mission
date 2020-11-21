package com.github.nagyesta.abortmission.core.telemetry.stats;

import com.github.nagyesta.abortmission.core.telemetry.StageResult;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Contains aggregated statistical information related to a group of measurements.
 * Can be the total of the test suite or a single test method depending on the
 * containing class.
 */
public final class AggregatedLaunchStats {

    private final LocalDateTime minStart;
    private final LocalDateTime maxEnd;
    private final StageResult worstResult;
    private final int count;
    private final int sumDuration;
    private final Integer minDuration;
    private final Integer maxDuration;
    private final Double avgDuration;
    private final Map<StageResult, Integer> resultCount;

    /**
     * Creates a new instance and merges information from the provided child
     * instances found in the set.
     *
     * @param childStats The set of child instances.
     */
    public AggregatedLaunchStats(final Set<AggregatedLaunchStats> childStats) {
        Objects.requireNonNull(childStats, "ChildStats cannot be null.");
        this.minStart = childStats.stream()
                .map(AggregatedLaunchStats::getMinStart)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder()).orElse(null);
        this.maxEnd = childStats.stream()
                .map(AggregatedLaunchStats::getMaxEnd)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder()).orElse(null);
        this.worstResult = childStats.stream()
                .map(AggregatedLaunchStats::getWorstResult)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder()).orElse(StageResult.SUPPRESSED);
        this.count = childStats.stream()
                .mapToInt(AggregatedLaunchStats::getCount)
                .sum();
        this.sumDuration = childStats.stream()
                .mapToInt(AggregatedLaunchStats::getSumDuration)
                .sum();
        this.minDuration = childStats.stream()
                .map(AggregatedLaunchStats::getMinDuration)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder()).orElse(null);
        this.maxDuration = childStats.stream()
                .map(AggregatedLaunchStats::getMaxDuration)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder()).orElse(null);
        if (count > 0) {
            final BigDecimal avg = new BigDecimal(sumDuration).divide(new BigDecimal(count), 1, BigDecimal.ROUND_HALF_EVEN);
            this.avgDuration = avg.doubleValue();
        } else {
            this.avgDuration = null;
        }
        final Map<StageResult, AtomicInteger> accumulator = new HashMap<>();
        childStats.forEach(aggregate -> aggregate.resultCount.forEach((stageResult, integer) -> {
            accumulator.computeIfAbsent(stageResult, key -> new AtomicInteger()).addAndGet(integer);
        }));
        this.resultCount = Collections.unmodifiableMap(accumulator.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get())));
    }

    /**
     * Parses measurements and collects statistical information based on them.
     *
     * @param measurements The measurements saved during test execution.
     */
    public AggregatedLaunchStats(final Collection<StageTimeMeasurement> measurements) {
        Objects.requireNonNull(measurements, "Measurements cannot be null.");
        final SortedSet<StageTimeMeasurement> inputMeasurements = filter(measurements);
        final EnumMap<StageResult, Integer> counts = new EnumMap<>(StageResult.class);
        inputMeasurements.stream()
                .collect(Collectors.groupingBy(StageTimeMeasurement::getResult))
                .forEach(((stageResult, measurementList) -> counts.put(stageResult, measurementList.size())));
        this.resultCount = Collections.unmodifiableMap(counts);
        this.worstResult = this.resultCount.keySet().stream().min(Comparator.naturalOrder()).orElse(StageResult.SUPPRESSED);
        this.minStart = inputMeasurements.stream().map(StageTimeMeasurement::getStart)
                .min(Comparator.naturalOrder())
                .map(start -> LocalDateTime.ofInstant(Instant.ofEpochMilli(start), ZoneId.systemDefault()))
                .orElse(null);
        this.maxEnd = inputMeasurements.stream().map(StageTimeMeasurement::getEnd)
                .max(Comparator.naturalOrder())
                .map(start -> LocalDateTime.ofInstant(Instant.ofEpochMilli(start), ZoneId.systemDefault()))
                .orElse(null);
        this.count = inputMeasurements.size();
        this.minDuration = inputMeasurements.stream().map(StageTimeMeasurement::getDurationMillis)
                .min(Comparator.naturalOrder())
                .map(Long::intValue)
                .orElse(null);
        this.maxDuration = inputMeasurements.stream().map(StageTimeMeasurement::getDurationMillis)
                .max(Comparator.naturalOrder())
                .map(Long::intValue)
                .orElse(null);
        this.sumDuration = inputMeasurements.stream().map(StageTimeMeasurement::getDurationMillis)
                .mapToInt(Long::intValue)
                .sum();
        final OptionalDouble average = inputMeasurements.stream().map(StageTimeMeasurement::getDurationMillis)
                .mapToInt(Long::intValue)
                .average();
        if (average.isPresent()) {
            this.avgDuration = average.getAsDouble();
        } else {
            this.avgDuration = null;
        }
    }

    /**
     * Filters time series data and throws out duplicates of the same launch (in case
     * multiple matchers match at the same run). Keeps only the worst outcome per each
     * launch (The first item from: Fail, Abort, Suppress, Succeed).
     *
     * @param inputMeasurements The measurement list we want to filter.
     * @return The filtered set without duplicates.
     */
    public static SortedSet<StageTimeMeasurement> filter(final Collection<StageTimeMeasurement> inputMeasurements) {
        Objects.requireNonNull(inputMeasurements, "Input cannot be null.");
        final Map<UUID, List<StageTimeMeasurement>> uuidListMap = inputMeasurements.stream()
                .collect(Collectors.groupingBy(StageTimeMeasurement::getLaunchId));
        return uuidListMap.values().stream()
                .map(measurementList -> measurementList.stream().min(Comparator.comparing(StageTimeMeasurement::getResult)))
                .filter(Optional::isPresent)
                .map(Optional::get).collect(Collectors.toCollection(TreeSet::new));
    }

    public LocalDateTime getMinStart() {
        return minStart;
    }

    public LocalDateTime getMaxEnd() {
        return maxEnd;
    }

    public StageResult getWorstResult() {
        return worstResult;
    }

    public int getCount() {
        return count;
    }

    public Integer getMinDuration() {
        return minDuration;
    }

    public Integer getMaxDuration() {
        return maxDuration;
    }

    public int getSumDuration() {
        return sumDuration;
    }

    public Double getAvgDuration() {
        return avgDuration;
    }

    public Map<StageResult, Integer> getResultCount() {
        return resultCount;
    }
}
