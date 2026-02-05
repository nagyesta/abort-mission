package com.github.nagyesta.abortmission.reporting.json;

import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class ClassJsonTest {

    @Test
    void testStreamAllTimeMeasurementsShouldReturnEmptyStreamWhenBothSourcesAreNull() {
        //given
        final var underTest = new ClassJson();

        //when
        final var actual = underTest.streamAllTimeMeasurements();

        //then
        assertEquals(0, actual.count());
    }

    @Test
    void testStreamAllTimeMeasurementsShouldReturnEmptyStreamWhenBothSourcesAreEmpty() {
        //given
        final var underTest = new ClassJson();
        underTest.setCountdown(new StageLaunchStatsJson());
        underTest.setLaunches(new TreeMap<>());

        //when
        final var actual = underTest.streamAllTimeMeasurements();

        //then
        assertEquals(0, actual.count());
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    void testStreamAllTimeMeasurementsShouldReturnAllEntriesMergedIntoASortedStreamWhenBothSourcesHaveMeasurements() {
        //given
        final var underTest = new ClassJson();
        final var countdown = new StageLaunchStatsJson();
        final var countdown1 = getTestRunJson(1, 2, StageResultJson.SUCCESS);
        final var countdown2 = getTestRunJson(20, 22, StageResultJson.FAILURE);
        countdown.setTimeMeasurements(new TreeSet<>(Set.of(countdown1, countdown2)));
        underTest.setCountdown(countdown);
        final var launch1 = getTestRunJson(3, 4, StageResultJson.SUPPRESSED);
        final var launch2 = getTestRunJson(5, 10, StageResultJson.FAILURE);
        final var launch3 = getTestRunJson(12, 17, StageResultJson.SUCCESS);

        final var map = new TreeMap<String, StageLaunchStatsJson>();
        final var mission1 = new StageLaunchStatsJson();
        mission1.setTimeMeasurements(new TreeSet<>(Set.of(launch1, launch2)));
        final var mission2 = new StageLaunchStatsJson();
        mission2.setTimeMeasurements(new TreeSet<>(Set.of(launch3)));
        map.put("mission-1", mission1);
        map.put("mission-2", mission2);
        underTest.setLaunches(map);

        //when
        final var actual = underTest.streamAllTimeMeasurements();

        //then
        final var actualList = actual.toList();
        assertIterableEquals(new TreeSet<>(Set.of(countdown1, launch1, launch2, launch3, countdown2)), actualList);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    void testStreamAllTimeMeasurementsShouldReturnAllEntriesInASortedStreamWhenOnlyCountdownHasMeasurements() {
        //given
        final var underTest = new ClassJson();
        final var countdown = new StageLaunchStatsJson();
        final var countdown1 = getTestRunJson(10, 20, StageResultJson.SUCCESS);
        final var countdown2 = getTestRunJson(10, 19, StageResultJson.FAILURE);
        countdown.setTimeMeasurements(new TreeSet<>(Set.of(countdown1, countdown2)));
        underTest.setCountdown(countdown);

        underTest.setLaunches(new TreeMap<>());

        //when
        final var actual = underTest.streamAllTimeMeasurements();

        //then
        final var actualList = actual.toList();
        assertIterableEquals(new TreeSet<>(Set.of(countdown2, countdown1)), actualList);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    void testStreamAllTimeMeasurementsShouldReturnAllEntriesInASortedStreamWhenOnlyLaunchesHaveMeasurements() {
        //given
        final var underTest = new ClassJson();
        underTest.setCountdown(new StageLaunchStatsJson());
        final var launch1 = getTestRunJson(30, 44, StageResultJson.SUPPRESSED);
        final var launch2 = getTestRunJson(5, 10, StageResultJson.FAILURE);
        final var launch3 = getTestRunJson(12, 17, StageResultJson.SUCCESS);
        final var map = new TreeMap<String, StageLaunchStatsJson>();
        final var mission1 = new StageLaunchStatsJson();
        mission1.setTimeMeasurements(new TreeSet<>(Set.of(launch1, launch2)));
        final var mission2 = new StageLaunchStatsJson();
        mission2.setTimeMeasurements(new TreeSet<>(Set.of(launch3)));
        map.put("mission-1", mission1);
        map.put("mission-2", mission2);
        underTest.setLaunches(map);

        //when
        final var actual = underTest.streamAllTimeMeasurements();

        //then
        final var actualList = actual.toList();
        assertIterableEquals(new TreeSet<>(Set.of(launch2, launch3, launch1)), actualList);
    }

    private static TestRunJson getTestRunJson(
            final int start,
            final int end,
            final StageResultJson result) {
        final var launch3 = new TestRunJson();
        launch3.setStart(start);
        launch3.setEnd(end);
        launch3.setResult(result);
        launch3.setLaunchId(UUID.randomUUID());
        return launch3;
    }
}
