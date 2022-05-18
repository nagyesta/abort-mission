package com.github.nagyesta.abortmission.strongback.rmi.stats;

import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;
import com.github.nagyesta.abortmission.core.telemetry.StageResult;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import com.github.nagyesta.abortmission.strongback.base.StrongbackException;
import com.github.nagyesta.abortmission.strongback.rmi.server.RmiServerConstants;
import com.github.nagyesta.abortmission.strongback.rmi.service.LaunchStatisticsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.UUID;

import static org.mockito.Mockito.*;

@Tag("unit")
class RmiBackedStageStatisticsCollectorTest {

    private static final String CONTEXT = "context";
    private static final String MATCHER = "matcher";
    private static final StageTimeMeasurement ABORTED = new StageTimeMeasurement(
            UUID.randomUUID(),
            "testClassId",
            "testCaseId",
            StageResult.ABORT,
            0,
            1);

    @Test
    void testDoGetSnapshotShouldThrowStrongbackExceptionWhenCaughtException() throws Exception {
        //given
        final MissionHealthCheckMatcher matcher = mock(MissionHealthCheckMatcher.class);
        when(matcher.getName()).thenReturn(MATCHER);
        final LaunchStatisticsService service = mock(LaunchStatisticsService.class);
        when(service.getSnapshot(anyString(), anyString(), anyBoolean())).thenThrow(new RemoteException());
        final Registry registry = mock(Registry.class);
        when(registry.lookup(eq(RmiServerConstants.SERVICE_NAME))).thenReturn(service);

        final RmiBackedStageStatisticsCollector underTest = new RmiBackedStageStatisticsCollector(CONTEXT, matcher, registry, true);

        //when
        Assertions.assertThrows(StrongbackException.class, () -> underTest.doGetSnapshot(CONTEXT, matcher, true));

        //then + exception
        final InOrder inOrder = inOrder(registry, service, matcher);
        inOrder.verify(registry).lookup(eq(RmiServerConstants.SERVICE_NAME));
        inOrder.verify(service).getSnapshot(eq(CONTEXT), eq(MATCHER), eq(true));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void testDoFetchAllShouldThrowStrongbackExceptionWhenCaughtException() throws Exception {
        //given
        final MissionHealthCheckMatcher matcher = mock(MissionHealthCheckMatcher.class);
        when(matcher.getName()).thenReturn(MATCHER);
        final LaunchStatisticsService service = mock(LaunchStatisticsService.class);
        when(service.fetchMeasurementsFor(anyString(), anyString(), anyBoolean())).thenThrow(new RemoteException());
        final Registry registry = mock(Registry.class);
        when(registry.lookup(eq(RmiServerConstants.SERVICE_NAME))).thenReturn(service);

        final RmiBackedStageStatisticsCollector underTest = new RmiBackedStageStatisticsCollector(CONTEXT, matcher, registry, true);

        //when
        Assertions.assertThrows(StrongbackException.class, () -> underTest.doFetchAll(CONTEXT, matcher, true));

        //then + exception
        final InOrder inOrder = inOrder(registry, service, matcher);
        inOrder.verify(registry).lookup(eq(RmiServerConstants.SERVICE_NAME));
        inOrder.verify(service).fetchMeasurementsFor(eq(CONTEXT), eq(MATCHER), eq(true));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void testDoLogTimeMeasurementShouldThrowStrongbackExceptionWhenCaughtException() throws Exception {
        //given
        final MissionHealthCheckMatcher matcher = mock(MissionHealthCheckMatcher.class);
        when(matcher.getName()).thenReturn(MATCHER);
        final LaunchStatisticsService service = mock(LaunchStatisticsService.class);
        doThrow(new RemoteException())
                .when(service).insertStageTimeMeasurement(anyString(), anyString(), anyBoolean(), any(RmiStageTimeMeasurement.class));
        final Registry registry = mock(Registry.class);
        when(registry.lookup(eq(RmiServerConstants.SERVICE_NAME))).thenReturn(service);

        final RmiBackedStageStatisticsCollector underTest = new RmiBackedStageStatisticsCollector(CONTEXT, matcher, registry, true);

        //when
        Assertions.assertThrows(StrongbackException.class, () ->
                underTest.doLogTimeMeasurement(CONTEXT, matcher, true, ABORTED));

        //then + exception
        final InOrder inOrder = inOrder(registry, service, matcher);
        inOrder.verify(registry).lookup(eq(RmiServerConstants.SERVICE_NAME));
        inOrder.verify(service).insertStageTimeMeasurement(eq(CONTEXT), eq(MATCHER), eq(true), any(RmiStageTimeMeasurement.class));
        inOrder.verifyNoMoreInteractions();
    }
}
