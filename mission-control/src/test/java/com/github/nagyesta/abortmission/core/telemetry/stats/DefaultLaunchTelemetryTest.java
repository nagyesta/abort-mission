package com.github.nagyesta.abortmission.core.telemetry.stats;

import com.github.nagyesta.abortmission.core.AbortMissionCommandOps;
import com.github.nagyesta.abortmission.core.telemetry.converter.LaunchTelemetryConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.mockito.Mockito.*;

class DefaultLaunchTelemetryTest {

    @ParameterizedTest
    @NullSource
    void testConstructorShouldThrowExceptionWhenCalledWithNull(final LaunchTelemetryDataSource dataSource) {
        //given

        //when
        Assertions.assertThrows(NullPointerException.class, () -> new DefaultLaunchTelemetry(dataSource));

        //then + exception
    }

    @Test
    void testConstructorShouldCallFetchClassStatisticsWhenCalledWithValidData() {
        //given
        final var converter = mock(LaunchTelemetryConverter.class);
        final Map<String, AbortMissionCommandOps> nameSpaces = spy(new HashMap<>());
        final SortedMap<String, ClassTelemetry> classStats = new TreeMap<>();
        when(converter.processClassStatistics(same(nameSpaces))).thenReturn(classStats);
        final var dataSource = mock(LaunchTelemetryDataSource.class);
        when(dataSource.fetchClassStatistics()).thenReturn(classStats);

        //when
        final var actual = new DefaultLaunchTelemetry(dataSource);

        //then
        Assertions.assertNotNull(actual);
        Assertions.assertSame(classStats, actual.getClasses());

        final var inOrder = inOrder(dataSource);
        inOrder.verify(dataSource).fetchClassStatistics();
        inOrder.verifyNoMoreInteractions();
        verifyNoInteractions(nameSpaces);
    }

}
