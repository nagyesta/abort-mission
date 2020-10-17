package com.github.nagyesta.abortmission.core;

import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;

class LaunchSequenceTemplateTest {

    @Test
    void testPerformPreLaunchInitShouldEvaluateCountdownAbortConditionsWhenCalled() {
        //given
        final Set<MissionHealthCheckEvaluator> evaluators = new HashSet<>();
        final Set<MissionHealthCheckEvaluator> aborting = new HashSet<>();
        final MissionHealthCheckEvaluator aborting1 = mock(MissionHealthCheckEvaluator.class);
        aborting.add(aborting1);
        final MissionHealthCheckEvaluator aborting2 = mock(MissionHealthCheckEvaluator.class);
        aborting.add(aborting2);
        final MissionHealthCheckEvaluator healthy = mock(MissionHealthCheckEvaluator.class);
        evaluators.add(healthy);
        evaluators.addAll(aborting);
        aborting.forEach(evaluator -> when(evaluator.shouldAbortCountdown()).thenReturn(true));
        final LaunchSequenceTemplate underTest = new LaunchSequenceTemplate(() -> {
        }, c -> evaluators, m -> null);

        //when
        underTest.performPreLaunchInit(LaunchSequenceTemplate.class);

        //then
        evaluators.forEach(evaluator -> {
            verify(evaluator, never()).shouldAbort();
            verify(evaluator).shouldAbortCountdown();
            verify(evaluator).logCountdownStarted();
        });
        aborting.forEach(evaluator -> verify(evaluator).logCountdownAborted());
    }

    @Test
    void testPreLaunchInitCompleteShouldEvaluateMissionAbortConditionsWhenCalled() {
        //given
        final Set<MissionHealthCheckEvaluator> evaluators = new HashSet<>();
        final Set<MissionHealthCheckEvaluator> aborting = new HashSet<>();
        final MissionHealthCheckEvaluator aborting1 = mock(MissionHealthCheckEvaluator.class);
        aborting.add(aborting1);
        final MissionHealthCheckEvaluator aborting2 = mock(MissionHealthCheckEvaluator.class);
        aborting.add(aborting2);
        final MissionHealthCheckEvaluator healthy = mock(MissionHealthCheckEvaluator.class);
        evaluators.add(healthy);
        evaluators.addAll(aborting);
        aborting.forEach(evaluator -> when(evaluator.shouldAbort()).thenReturn(true));
        final LaunchSequenceTemplate underTest = new LaunchSequenceTemplate(() -> {
        }, c -> null, m -> null);

        //when
        underTest.preLaunchInitComplete(() -> false, evaluators);

        //then
        evaluators.forEach(evaluator -> {
            verify(evaluator).shouldAbort();
            verify(evaluator, never()).shouldAbortCountdown();
        });
        aborting.forEach(evaluator -> {
            verify(evaluator).logLaunchImminent();
            verify(evaluator).logMissionAbort();
        });
    }

    @Test
    void testEvaluateAndAbortIfNeededShouldAbortOnTheMatchingEvaluatorsOnlyWhenCalled() {
        //given
        final Set<MissionHealthCheckEvaluator> evaluators = new HashSet<>();
        final MissionHealthCheckEvaluator healthy = mock(MissionHealthCheckEvaluator.class);
        evaluators.add(healthy);
        final LaunchSequenceTemplate underTest = new LaunchSequenceTemplate(() -> {
        }, c -> null, m -> null);

        //when
        underTest.evaluateAndAbortIfNeeded(evaluators,
                MissionHealthCheckEvaluator::shouldAbort,
                MissionHealthCheckEvaluator::logMissionAbort);

        //then
        evaluators.forEach(evaluator -> verify(evaluator).shouldAbort());
    }
}
