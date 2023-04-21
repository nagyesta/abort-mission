package com.github.nagyesta.abortmission.core.telemetry.stats;

import com.github.nagyesta.abortmission.core.AbortMissionCommandOps;
import com.github.nagyesta.abortmission.core.telemetry.converter.LaunchTelemetryConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InOrder;

import java.util.*;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

class DefaultLaunchTelemetryTest {

    private static Stream<Arguments> nullProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, null))
                .add(Arguments.of(new LaunchTelemetryConverter(), null))
                .add(Arguments.of(null, Collections.emptyMap()))
                .build();
    }

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
        final LaunchTelemetryConverter converter = mock(LaunchTelemetryConverter.class);
        final Map<String, AbortMissionCommandOps> nameSpaces = spy(new HashMap<>());
        final SortedMap<String, ClassTelemetry> classStats = new TreeMap<>();
        when(converter.processClassStatistics(same(nameSpaces))).thenReturn(classStats);
        final LaunchTelemetryDataSource dataSource = mock(LaunchTelemetryDataSource.class);
        when(dataSource.fetchClassStatistics()).thenReturn(classStats);

        //when
        final DefaultLaunchTelemetry actual = new DefaultLaunchTelemetry(dataSource);

        //then
        Assertions.assertNotNull(actual);
        Assertions.assertSame(classStats, actual.getClasses());

        final InOrder inOrder = inOrder(dataSource);
        inOrder.verify(dataSource).fetchClassStatistics();
        inOrder.verifyNoMoreInteractions();
        verifyNoInteractions(nameSpaces);
    }

}
