package com.github.nagyesta.abortmission.core.telemetry.converter;

import com.github.nagyesta.abortmission.core.AbortMissionCommandOps;
import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;
import com.github.nagyesta.abortmission.core.healthcheck.impl.AbstractMissionHealthCheckEvaluator;
import com.github.nagyesta.abortmission.core.healthcheck.impl.MissionStatisticsCollector;
import com.github.nagyesta.abortmission.core.matcher.impl.BaseMatcher;
import com.github.nagyesta.abortmission.core.telemetry.StageTimeMeasurement;
import com.github.nagyesta.abortmission.core.telemetry.stats.AbstractTelemetryTest;
import com.github.nagyesta.abortmission.core.telemetry.stats.ClassTelemetry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

class LaunchTelemetryConverterTest extends AbstractTelemetryTest {

    private static final String MATCHER_1 = "matcher1";
    private static final String MATCHER_2 = "matcher2";
    private static final String MATCHER_3 = "matcher3";

    @Test
    void testProcessClassStatisticsShould() {
        //given
        final Map<String, AbortMissionCommandOps> nameSpaces = new HashMap<>();
        final LaunchTelemetryConverter underTest = spy(new LaunchTelemetryConverter());
        final Stream<MissionHealthCheckEvaluator> stream = Stream.<MissionHealthCheckEvaluator>builder()
                .add(new SimpleEvaluator(MATCHER_1,
                        Arrays.asList(FIRST_FAIL_1_10, FIRST_PASS_1_10),
                        Collections.emptyList()))
                .add(new SimpleEvaluator(MATCHER_2,
                        Arrays.asList(FIRST_FAIL_1_10, FIRST_PASS_1_10),
                        Collections.singletonList(SECOND_PASS_2_20)))
                .add(new SimpleEvaluator(MATCHER_3,
                        Collections.emptyList(),
                        Arrays.asList(THIRD_ABORT_5_5, THIRD_PASS_5_5, THIRD_SUPPRESS_5_5)))
                .build();
        doReturn(stream).when(underTest).missionHealthCheckEvaluatorStream(same(nameSpaces));

        //when
        final SortedMap<String, ClassTelemetry> actual = underTest.processClassStatistics(nameSpaces);

        //then
        Assertions.assertNotNull(actual);
        verify(underTest).missionHealthCheckEvaluatorStream(same(nameSpaces));
        Assertions.assertIterableEquals(Collections.singleton(CLASS), actual.keySet());
        final ClassTelemetry classTelemetry = actual.get(CLASS);
        Assertions.assertEquals(CLASS, classTelemetry.getClassName());
        Assertions.assertIterableEquals(Arrays.asList(MATCHER_1, MATCHER_2), classTelemetry.getCountdown().getMatcherNames());
        Assertions.assertIterableEquals(Arrays.asList(MATCHER_2, MATCHER_3), classTelemetry.getLaunches().get(METHOD).getMatcherNames());
    }

    private static final class SimpleMatcher extends BaseMatcher {

        private final String name;

        private SimpleMatcher(final String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public MatchCriteria getMatchCriteria() {
            return null;
        }

        @Override
        public boolean matches(final Object component) {
            return false;
        }
    }

    private static final class SimpleEvaluator extends AbstractMissionHealthCheckEvaluator {

        private SimpleEvaluator(final String matcherName,
                                final List<StageTimeMeasurement> countdown,
                                final List<StageTimeMeasurement> mission) {
            super(new SimpleMatcher(matcherName), new MissionStatisticsCollector(new SimpleMatcher(matcherName)));
            countdown.forEach(m -> this.countdownLogger().logAndIncrement(m));
            mission.forEach(m -> this.missionLogger().logAndIncrement(m));
        }

        @Override
        protected boolean shouldAbortInternal() {
            return false;
        }

        @Override
        protected boolean shouldAbortCountdownInternal() {
            return false;
        }

        @Override
        public int getBurnInTestCount() {
            return 0;
        }
    }
}
