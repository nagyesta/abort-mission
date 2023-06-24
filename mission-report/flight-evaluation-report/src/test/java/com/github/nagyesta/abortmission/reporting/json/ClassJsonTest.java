package com.github.nagyesta.abortmission.reporting.json;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class ClassJsonTest {

    @Test
    void testStreamAllTimeMeasurementsShouldReturnEmptyStreamWhenBothSourcesAreNull() {
        //given
        final ClassJson underTest = new ClassJson();

        //when
        final Stream<TestRunJson> actual = underTest.streamAllTimeMeasurements();

        //then
        assertEquals(0, actual.count());
    }

    @Test
    void testStreamAllTimeMeasurementsShouldReturnEmptyStreamWhenBothSourcesAreEmpty() {
        //given
        final ClassJson underTest = new ClassJson();
        underTest.setCountdown(new StageLaunchStatsJson());
        underTest.setLaunches(new TreeMap<>());

        //when
        final Stream<TestRunJson> actual = underTest.streamAllTimeMeasurements();

        //then
        assertEquals(0, actual.count());
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    void testStreamAllTimeMeasurementsShouldReturnAllEntriesMergedIntoASortedStreamWhenBothSourcesHaveMeasurements() {
        //given
        final ClassJson underTest = new ClassJson();
        final StageLaunchStatsJson countdown = new StageLaunchStatsJson();
        final TestRunJson countdown1 = getTestRunJson(1, 2, StageResultJson.SUCCESS);
        final TestRunJson countdown2 = getTestRunJson(20, 22, StageResultJson.FAILURE);
        countdown.setTimeMeasurements(new TreeSet<>(Set.of(countdown1, countdown2)));
        underTest.setCountdown(countdown);
        final TestRunJson launch1 = getTestRunJson(3, 4, StageResultJson.SUPPRESSED);
        final TestRunJson launch2 = getTestRunJson(5, 10, StageResultJson.FAILURE);
        final TestRunJson launch3 = getTestRunJson(12, 17, StageResultJson.SUCCESS);

        final TreeMap<String, StageLaunchStatsJson> map = new TreeMap<>();
        final StageLaunchStatsJson mission1 = new StageLaunchStatsJson();
        mission1.setTimeMeasurements(new TreeSet<>(Set.of(launch1, launch2)));
        final StageLaunchStatsJson mission2 = new StageLaunchStatsJson();
        mission2.setTimeMeasurements(new TreeSet<>(Set.of(launch3)));
        map.put("mission-1", mission1);
        map.put("mission-2", mission2);
        underTest.setLaunches(map);

        //when
        final Stream<TestRunJson> actual = underTest.streamAllTimeMeasurements();

        //then
        final List<TestRunJson> actualList = actual.collect(Collectors.toList());
        assertIterableEquals(new TreeSet<>(Set.of(countdown1, launch1, launch2, launch3, countdown2)), actualList);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    void testStreamAllTimeMeasurementsShouldReturnAllEntriesInASortedStreamWhenOnlyCountdownHasMeasurements() {
        //given
        final ClassJson underTest = new ClassJson();
        final StageLaunchStatsJson countdown = new StageLaunchStatsJson();
        final TestRunJson countdown1 = getTestRunJson(10, 20, StageResultJson.SUCCESS);
        final TestRunJson countdown2 = getTestRunJson(10, 19, StageResultJson.FAILURE);
        countdown.setTimeMeasurements(new TreeSet<>(Set.of(countdown1, countdown2)));
        underTest.setCountdown(countdown);

        underTest.setLaunches(new TreeMap<>());

        //when
        final Stream<TestRunJson> actual = underTest.streamAllTimeMeasurements();

        //then
        final List<TestRunJson> actualList = actual.collect(Collectors.toList());
        assertIterableEquals(new TreeSet<>(Set.of(countdown2, countdown1)), actualList);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    void testStreamAllTimeMeasurementsShouldReturnAllEntriesInASortedStreamWhenOnlyLaunchesHaveMeasurements() {
        //given
        final ClassJson underTest = new ClassJson();
        underTest.setCountdown(new StageLaunchStatsJson());
        final TestRunJson launch1 = getTestRunJson(30, 44, StageResultJson.SUPPRESSED);
        final TestRunJson launch2 = getTestRunJson(5, 10, StageResultJson.FAILURE);
        final TestRunJson launch3 = getTestRunJson(12, 17, StageResultJson.SUCCESS);
        final TreeMap<String, StageLaunchStatsJson> map = new TreeMap<>();
        final StageLaunchStatsJson mission1 = new StageLaunchStatsJson();
        mission1.setTimeMeasurements(new TreeSet<>(Set.of(launch1, launch2)));
        final StageLaunchStatsJson mission2 = new StageLaunchStatsJson();
        mission2.setTimeMeasurements(new TreeSet<>(Set.of(launch3)));
        map.put("mission-1", mission1);
        map.put("mission-2", mission2);
        underTest.setLaunches(map);

        //when
        final Stream<TestRunJson> actual = underTest.streamAllTimeMeasurements();

        //then
        final List<TestRunJson> actualList = actual.collect(Collectors.toList());
        assertIterableEquals(new TreeSet<>(Set.of(launch2, launch3, launch1)), actualList);
    }

    private static TestRunJson getTestRunJson(final int start, final int end, final StageResultJson result) {
        final TestRunJson launch3 = new TestRunJson();
        launch3.setStart(start);
        launch3.setEnd(end);
        launch3.setResult(result);
        launch3.setLaunchId(UUID.randomUUID());
        return launch3;
    }
}
