package com.github.nagyesta.abortmission.strongback.base;

import com.github.nagyesta.abortmission.core.healthcheck.StageStatisticsSnapshot;
import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;
import com.github.nagyesta.abortmission.core.telemetry.StageResult;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurementBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

class ExternalStageStatisticsCollectorTest {

    private static final String CONTEXT = "context";
    private static final String MATCHER = "matcher";

    @Test
    void testGetSnapshotShouldCallDoGetSnapshotWhenCalled() {
        //given
        final MissionHealthCheckMatcher matcher = mock(MissionHealthCheckMatcher.class);
        when(matcher.getName()).thenReturn(MATCHER);
        final ExternalStageStatisticsCollector underTest =
                spy(new NoOpExternalStageStatisticsCollector(CONTEXT, matcher, true));

        //when
        underTest.getSnapshot();

        //then
        verify(underTest).getSnapshot();
    }

    @Test
    void testTimeSeriesStreamShouldCallDoFetchAllWhenCalled() {
        //given
        final MissionHealthCheckMatcher matcher = mock(MissionHealthCheckMatcher.class);
        when(matcher.getName()).thenReturn(MATCHER);
        final ExternalStageStatisticsCollector underTest =
                spy(new NoOpExternalStageStatisticsCollector(CONTEXT, matcher, true));

        //when
        underTest.timeSeriesStream();

        //then
        verify(underTest).doFetchAll(eq(CONTEXT), eq(matcher), eq(true));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testLogTimeMeasurementShouldCallDoLogTimeMeasurementWhenCalled(final boolean countdown) {
        //given
        final StageTimeMeasurement measurement = StageTimeMeasurementBuilder.builder()
                .setLaunchId(UUID.randomUUID())
                .setTestClassId(ExternalStageStatisticsCollectorTest.class.getName())
                .setTestCaseId(StageTimeMeasurement.CLASS_ONLY)
                .setDisplayName(StageTimeMeasurement.CLASS_ONLY)
                .setStart(0)
                .setEnd(0)
                .setThreadName(Thread.currentThread().getName())
                .setResult(StageResult.SUCCESS)
                .build();
        final MissionHealthCheckMatcher matcher = mock(MissionHealthCheckMatcher.class);
        when(matcher.getName()).thenReturn(MATCHER);
        final ExternalStageStatisticsCollector underTest =
                spy(new NoOpExternalStageStatisticsCollector(CONTEXT, matcher, countdown));

        //when
        underTest.logTimeMeasurement(measurement);

        //then
        verify(underTest).doLogTimeMeasurement(eq(CONTEXT), eq(matcher), eq(countdown), same(measurement));
    }

    private static class NoOpExternalStageStatisticsCollector extends ExternalStageStatisticsCollector {
        NoOpExternalStageStatisticsCollector(final String contextName,
                                             final MissionHealthCheckMatcher matcher,
                                             final boolean countdown) {
            super(contextName, matcher, countdown);
        }

        @Override
        protected StageStatisticsSnapshot doGetSnapshot(final String contextName,
                                                        final MissionHealthCheckMatcher matcher,
                                                        final boolean countdown) {
            return null;
        }

        @Override
        protected List<StageTimeMeasurement> doFetchAll(final String contextName,
                                                        final MissionHealthCheckMatcher matcher,
                                                        final boolean countdown) {
            return Collections.emptyList();
        }

        @Override
        protected void doLogTimeMeasurement(final String contextName,
                                            final MissionHealthCheckMatcher matcher,
                                            final boolean countdown,
                                            final StageTimeMeasurement measurement) {

        }
    }

}
