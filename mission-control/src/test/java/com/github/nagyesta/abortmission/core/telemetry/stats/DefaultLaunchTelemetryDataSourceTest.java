package com.github.nagyesta.abortmission.core.telemetry.stats;

import com.github.nagyesta.abortmission.core.AbortMissionCommandOps;
import com.github.nagyesta.abortmission.core.telemetry.converter.LaunchTelemetryConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Stream;

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
    void testConstructorShouldThrowExceptionWhenCalledWithNull(
            final LaunchTelemetryConverter converter,
            final Map<String, AbortMissionCommandOps> nameSpaces) {
        //given

        //when
        Assertions.assertThrows(NullPointerException.class, () -> new DefaultLaunchTelemetryDataSource(converter, nameSpaces));

        //then + exception
    }

    @Test
    void testFetchClassStatisticsShouldCallConverterWhenConstructedWithValidData() {
        //given
        final var converter = mock(LaunchTelemetryConverter.class);
        final Map<String, AbortMissionCommandOps> nameSpaces = spy(new HashMap<>());
        final SortedMap<String, ClassTelemetry> classStats = spy(new TreeMap<>());
        when(converter.processClassStatistics(same(nameSpaces))).thenReturn(classStats);
        final var underTest = new DefaultLaunchTelemetryDataSource(converter, nameSpaces);

        //when
        final var actual = underTest.fetchClassStatistics();

        //then
        Assertions.assertNotNull(actual);
        Assertions.assertSame(classStats, actual);

        final var inOrder = inOrder(converter, classStats);
        inOrder.verify(converter).processClassStatistics(same(nameSpaces));
        inOrder.verifyNoMoreInteractions();
        verifyNoInteractions(nameSpaces);
    }

    @Test
    void testResolveContextMapShouldResolveContextsFromCommandOpsWhenCalled() {
        //given
        final var names = AbortMissionCommandOps.contextNames();

        //when
        final var actual = DefaultLaunchTelemetryDataSource.resolveContextMap();

        //then
        Assertions.assertTrue(actual.keySet().containsAll(names));
        actual.forEach((name, context) -> {
            Assertions.assertNotNull(context);
            Assertions.assertTrue(names.contains(name));
        });
    }
}
