package com.github.nagyesta.abortmission.core.telemetry.stats;

import com.github.nagyesta.abortmission.core.AbortMissionCommandOps;
import com.github.nagyesta.abortmission.core.telemetry.converter.LaunchTelemetryConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InOrder;

import java.util.*;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

class DefaultLaunchTelemetryDataSourceTest {

    private static Stream<Arguments> nullProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, null))
                .add(Arguments.of(new LaunchTelemetryConverter(), null))
                .add(Arguments.of(null, Collections.emptyMap()))
                .build();
    }

    @ParameterizedTest
    @MethodSource("nullProvider")
    void testConstructorShouldThrowExceptionWhenCalledWithNull(final LaunchTelemetryConverter converter,
                                                               final Map<String, AbortMissionCommandOps> nameSpaces) {
        //given

        //when
        Assertions.assertThrows(NullPointerException.class, () -> new DefaultLaunchTelemetryDataSource(converter, nameSpaces));

        //then + exception
    }

    @Test
    void testFetchClassStatisticsShouldCallConverterWhenConstructedWithValidData() {
        //given
        final LaunchTelemetryConverter converter = mock(LaunchTelemetryConverter.class);
        final Map<String, AbortMissionCommandOps> nameSpaces = spy(new HashMap<>());
        final SortedMap<String, ClassTelemetry> classStats = spy(new TreeMap<>());
        when(converter.processClassStatistics(same(nameSpaces))).thenReturn(classStats);
        final DefaultLaunchTelemetryDataSource underTest = new DefaultLaunchTelemetryDataSource(converter, nameSpaces);

        //when
        final SortedMap<String, ClassTelemetry> actual = underTest.fetchClassStatistics();

        //then
        Assertions.assertNotNull(actual);
        Assertions.assertSame(classStats, actual);

        final InOrder inOrder = inOrder(converter, classStats);
        inOrder.verify(converter).processClassStatistics(same(nameSpaces));
        inOrder.verifyNoMoreInteractions();
        verifyNoInteractions(nameSpaces);
    }

    @Test
    void testResolveContextMapShouldResolveContextsFromCommandOpsWhenCalled() {
        //given
        final Set<String> names = AbortMissionCommandOps.contextNames();

        //when
        final Map<String, AbortMissionCommandOps> actual = DefaultLaunchTelemetryDataSource.resolveContextMap();

        //then
        Assertions.assertTrue(actual.keySet().containsAll(names));
        actual.forEach((name, context) -> {
            Assertions.assertNotNull(context);
            Assertions.assertTrue(names.contains(name));
        });
    }
}
