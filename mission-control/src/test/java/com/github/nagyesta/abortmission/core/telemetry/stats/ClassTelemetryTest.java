package com.github.nagyesta.abortmission.core.telemetry.stats;

import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import com.github.nagyesta.abortmission.core.telemetry.converter.ClassTelemetryConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InOrder;

import java.util.*;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

class ClassTelemetryTest extends AbstractTelemetryTest {

    private static Stream<Arguments> nullProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, null, null, null))
                .add(Arguments.of(new ClassTelemetryConverter(), null, null, null))
                .add(Arguments.of(null, CLASS, null, null))
                .add(Arguments.of(null, null, Collections.emptySet(), null))
                .add(Arguments.of(null, null, null, Collections.emptyMap()))
                .add(Arguments.of(new ClassTelemetryConverter(), CLASS, Collections.emptySet(), null))
                .add(Arguments.of(new ClassTelemetryConverter(), CLASS, null, Collections.emptyMap()))
                .add(Arguments.of(new ClassTelemetryConverter(), null, Collections.emptySet(), Collections.emptyMap()))
                .add(Arguments.of(null, CLASS, Collections.emptySet(), Collections.emptyMap()))
                .build();
    }

    @ParameterizedTest
    @MethodSource("nullProvider")
    void testConstructorShouldThrowExceptionWhenCalledWithNulls(final ClassTelemetryConverter converter,
                                                                final String className,
                                                                final Collection<StageTimeMeasurement> measurements,
                                                                final Map<String, Set<String>> matcherNames) {
        //given

        //when
        Assertions.assertThrows(NullPointerException.class, () -> new ClassTelemetry(converter, className, measurements, matcherNames));

        //then + exception
    }

    @Test
    void testConstructorShouldCallConverterWhenCalledWithValidInput() {
        //given
        final ClassTelemetryConverter converter = mock(ClassTelemetryConverter.class);
        final Collection<StageTimeMeasurement> measurements = spy(new HashSet<>());
        final Map<String, Set<String>> matcherNames = spy(new HashMap<>());

        final Map<String, List<StageTimeMeasurement>> byMethods = spy(new HashMap<>());
        when(converter.partitionByMethods(same(measurements))).thenReturn(byMethods);

        final StageLaunchStats countdown = new StageLaunchStats(Collections.emptySortedSet(), Collections.emptySet());
        when(converter.processCountdownStats(same(matcherNames), same(byMethods))).thenReturn(countdown);

        final Map<String, StageLaunchStats> methodStats = new HashMap<>();
        when(converter.processLaunchStats(same(matcherNames), same(byMethods))).thenReturn(methodStats);

        //when
        final ClassTelemetry actual = new ClassTelemetry(converter, CLASS, measurements, matcherNames);

        //then
        Assertions.assertNotNull(actual);
        Assertions.assertSame(countdown, actual.getCountdown());
        Assertions.assertSame(methodStats, actual.getLaunches());
        Assertions.assertSame(CLASS, actual.getClassName());

        final InOrder inOrder = inOrder(converter);
        inOrder.verify(converter).partitionByMethods(same(measurements));
        inOrder.verify(converter).processCountdownStats(same(matcherNames), same(byMethods));
        inOrder.verify(converter).processLaunchStats(same(matcherNames), same(byMethods));
        inOrder.verifyNoMoreInteractions();
        verifyNoInteractions(byMethods, matcherNames, measurements);
    }
}
