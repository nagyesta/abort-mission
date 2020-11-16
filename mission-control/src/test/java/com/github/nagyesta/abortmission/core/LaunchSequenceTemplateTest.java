package com.github.nagyesta.abortmission.core;

import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;
import com.github.nagyesta.abortmission.core.healthcheck.StatisticsLogger;
import com.github.nagyesta.abortmission.core.healthcheck.impl.StageStatisticsCollector;
import com.github.nagyesta.abortmission.core.telemetry.watch.StageTimeStopwatch;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
class LaunchSequenceTemplateTest {

    private static final String METHOD_NAME = "nonSuppressedThrowableProvider";
    private static final String SHOULDN_T_BE_CALLED = "Shouldn't be called";

    static Stream<Arguments> nonSuppressedThrowableProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(
                        Optional.of(new IllegalArgumentException()),
                        Collections.emptySet()))
                .add(Arguments.of(
                        Optional.of(new IllegalArgumentException()),
                        Collections.singleton(IllegalStateException.class)))
                .add(Arguments.of(
                        Optional.empty(),
                        Collections.emptySet()))
                .build();
    }

    @Test
    void testPerformPreLaunchInitShouldEvaluateCountdownAbortConditionsWhenCalled() {
        //given
        final Set<MissionHealthCheckEvaluator> evaluators = addEvaluatorMockWithSpyLogger(new HashSet<>(), false);
        final Set<MissionHealthCheckEvaluator> aborting = new HashSet<>();
        addEvaluatorMockWithSpyLogger(aborting, true);
        addEvaluatorMockWithSpyLogger(aborting, true);
        evaluators.addAll(aborting);
        final LaunchSequenceTemplate underTest = new LaunchSequenceTemplate(() -> {
        }, c -> evaluators, m -> null);

        //when
        underTest.performPreLaunchInit(LaunchSequenceTemplate.class);

        //then
        evaluators.forEach(evaluator -> {
            verify(evaluator, never()).shouldAbort();
            verify(evaluator).shouldAbortCountdown();
        });
        aborting.forEach(evaluator -> {
            verify(evaluator).countdownLogger();
            verifyOnlyIncrementAbortedCalled(evaluator.countdownLogger());
            verify(evaluator, never()).missionLogger();
        });
    }

    @Test
    void testPreLaunchInitCompleteShouldEvaluateMissionAbortConditionsWhenCalled() {
        //given
        final Set<MissionHealthCheckEvaluator> evaluators = addEvaluatorMockWithSpyLogger(new HashSet<>(), false);
        final Set<MissionHealthCheckEvaluator> aborting = new HashSet<>();
        addEvaluatorMockWithSpyLogger(aborting, true);
        addEvaluatorMockWithSpyLogger(aborting, true);
        evaluators.addAll(aborting);
        final LaunchSequenceTemplate underTest = underTestWithNullFunctions();

        //when
        underTest.evaluateLaunchAbort(evaluators, new StageTimeStopwatch(getClass()), () -> false);

        //then
        evaluators.forEach(evaluator -> {
            verify(evaluator).shouldAbort();
            verify(evaluator, never()).shouldAbortCountdown();
        });
        aborting.forEach(evaluator -> {
            verify(evaluator, never()).countdownLogger();
            verify(evaluator).missionLogger();
            verifyOnlyIncrementAbortedCalled(evaluator.missionLogger());
        });
    }

    @Test
    void testEvaluateAndAbortIfNeededShouldAbortOnlyOnTheMatchingEvaluatorsWhenCalled() {
        //given
        final Set<MissionHealthCheckEvaluator> evaluators = addEvaluatorMockWithSpyLogger(new HashSet<>(), false);
        final LaunchSequenceTemplate underTest = underTestWithNullFunctions();

        //when
        underTest.evaluateAndAbortIfNeeded(evaluators,
                false,
                new StageTimeStopwatch(getClass()).stop(),
                MissionHealthCheckEvaluator::shouldAbort,
                MissionHealthCheckEvaluator::missionLogger);

        //then
        evaluators.forEach(evaluator -> {
            verify(evaluator).shouldAbort();
            verify(evaluator, never()).missionLogger();
        });
    }

    @Test
    void testEvaluateLaunchAbortShouldSuppressAllWhenCalledAndSomeWouldAbort() {
        //given
        final Set<MissionHealthCheckEvaluator> evaluators = addEvaluatorMockWithSpyLogger(new HashSet<>(), false);
        addEvaluatorMockWithSpyLogger(evaluators, true);
        final LaunchSequenceTemplate underTest = underTestWithNullFunctions();

        //when
        final Optional<StageTimeStopwatch> stopwatch = underTest.evaluateLaunchAbort(evaluators, new StageTimeStopwatch(getClass()),
                () -> true
        );

        //then
        Assertions.assertNotNull(stopwatch);
        Assertions.assertFalse(stopwatch.isPresent());
        evaluators.forEach(evaluator -> {
            verify(evaluator).shouldAbort();
            verify(evaluator).missionLogger();
            verifyOnlyIncrementSuppressedCalled(evaluator.missionLogger());
        });
    }

    @Test
    void testCountdownCompletedSuccessfullyShouldLogSuccessIfStopWatchIsPresent() {
        //given
        final Set<MissionHealthCheckEvaluator> evaluators = addEvaluatorMockWithSpyLogger(new HashSet<>(), false);
        final LaunchSequenceTemplate underTest = underTestWithNullFunctions();

        //when
        underTest.countdownCompletedSuccessfully(evaluators, presentOptionalStopwatch());

        //then
        evaluators.forEach(evaluator -> {
            verify(evaluator).countdownLogger();
            verifyOnlyIncrementSucceededCalled(evaluator.countdownLogger());
            verify(evaluator, never()).missionLogger();
        });
    }

    @Test
    void testCountdownCompletedSuccessfullyShouldNotLogSuccessIfStopWatchIsAbsent() {
        //given
        final Set<MissionHealthCheckEvaluator> evaluators = addEvaluatorMockWithSpyLogger(new HashSet<>(), false);
        final LaunchSequenceTemplate underTest = underTestWithNullFunctions();

        //when
        underTest.countdownCompletedSuccessfully(evaluators, Optional.empty());

        //then
        evaluators.forEach(evaluator -> {
            verify(evaluator, never()).countdownLogger();
            verify(evaluator, never()).missionLogger();
        });
    }

    @Test
    void testMissionCompletedSuccessfullyShouldLogSuccessIfStopWatchIsPresent() {
        //given
        final Set<MissionHealthCheckEvaluator> evaluators = addEvaluatorMockWithSpyLogger(new HashSet<>(), false);
        final LaunchSequenceTemplate underTest = underTestWithNullFunctions();

        //when
        underTest.missionCompletedSuccessfully(evaluators, presentOptionalStopwatch());

        //then
        evaluators.forEach(evaluator -> {
            verify(evaluator, never()).countdownLogger();
            verify(evaluator).missionLogger();
            verifyOnlyIncrementSucceededCalled(evaluator.missionLogger());
        });
    }

    @Test
    void testMissionCompletedSuccessfullyShouldNotLogSuccessIfStopWatchIsAbsent() {
        //given
        final Set<MissionHealthCheckEvaluator> evaluators = addEvaluatorMockWithSpyLogger(new HashSet<>(), false);
        final LaunchSequenceTemplate underTest = underTestWithNullFunctions();

        //when
        underTest.missionCompletedSuccessfully(evaluators, Optional.empty());

        //then
        evaluators.forEach(evaluator -> {
            verify(evaluator, never()).countdownLogger();
            verify(evaluator, never()).missionLogger();
        });
    }

    @ParameterizedTest
    @MethodSource("nonSuppressedThrowableProvider")
    void testCountdownFailureDetectedShouldLogFailureWhenCalledWithNonSuppressedThrowable(
            final Optional<Throwable> throwable, final Set<Class<? extends Exception>> suppressed) {
        //given
        final Set<MissionHealthCheckEvaluator> evaluators = addEvaluatorMockWithSpyLogger(new HashSet<>(), false);
        final LaunchSequenceTemplate underTest = underTestWithNullFunctions();

        //when
        underTest.countdownFailureDetected(evaluators, presentOptionalStopwatch(), throwable, suppressed);

        //then
        evaluators.forEach(evaluator -> {
            verify(evaluator).countdownLogger();
            verifyOnlyIncrementFailedCalled(evaluator.countdownLogger());
            verify(evaluator, never()).missionLogger();
        });
    }

    @ParameterizedTest
    @MethodSource("nonSuppressedThrowableProvider")
    void testMissionFailureDetectedShouldLogFailureWhenCalledWithNonSuppressedThrowable(
            final Optional<Throwable> throwable, final Set<Class<? extends Exception>> suppressed) {
        //given
        final Set<MissionHealthCheckEvaluator> evaluators = addEvaluatorMockWithSpyLogger(new HashSet<>(), false);
        final LaunchSequenceTemplate underTest = underTestWithNullFunctions();

        //when
        underTest.missionFailureDetected(evaluators, presentOptionalStopwatch(), throwable, suppressed);

        //then
        evaluators.forEach(evaluator -> {
            verify(evaluator, never()).countdownLogger();
            verify(evaluator).missionLogger();
            verifyOnlyIncrementFailedCalled(evaluator.missionLogger());
        });
    }

    @Test
    void testMissionFailureDetectedShouldLogSuppressionWhenCalledWithSuppressedThrowable() {
        //given
        final Optional<Throwable> throwable = Optional.of(new IllegalArgumentException());
        final Set<Class<? extends Exception>> suppressed = Collections.singleton(IllegalArgumentException.class);
        final Set<MissionHealthCheckEvaluator> evaluators = addEvaluatorMockWithSpyLogger(new HashSet<>(), false);
        final LaunchSequenceTemplate underTest = underTestWithNullFunctions();

        //when
        underTest.missionFailureDetected(evaluators, presentOptionalStopwatch(), throwable, suppressed);

        //then
        evaluators.forEach(evaluator -> {
            verify(evaluator, never()).countdownLogger();
            verify(evaluator).missionLogger();
            verifyOnlyIncrementSuppressedCalled(evaluator.missionLogger());
        });
    }

    @Test
    void testLaunchGoNoGoShouldPerformPreLaunchInitWhenCalled() {
        //given
        final LaunchSequenceTemplate underTest = spy(underTestWithNullFunctions());
        doReturn(Optional.empty()).when(underTest).performPreLaunchInit(any());

        //when
        underTest.launchGoNoGo(this.getClass());

        //then
        verify(underTest).performPreLaunchInit(eq(this.getClass()));
    }

    @Test
    void testCountdownFailureShouldFindEvaluatorsAndSuppressedExceptionsWhenCalled() {
        //given
        final Set<MissionHealthCheckEvaluator> matching = Collections.singleton(mock(MissionHealthCheckEvaluator.class));
        final LaunchSequenceTemplate underTest = spyWithMethodMatchers(matching, null);

        //when
        underTest.countdownFailure(this.getClass(), Optional.empty(), Optional.empty());

        //then
        verify(underTest).countdownFailureDetected(same(matching), eq(Optional.empty()), eq(Optional.empty()), eq(Collections.emptySet()));
    }

    @Test
    void testCountdownSuccessShouldFindEvaluatorsWhenCalled() {
        //given
        final Set<MissionHealthCheckEvaluator> matching = Collections.singleton(mock(MissionHealthCheckEvaluator.class));
        final LaunchSequenceTemplate underTest = spyWithMethodMatchers(matching, null);

        //when
        underTest.countdownSuccess(this.getClass(), Optional.empty());

        //then
        verify(underTest).countdownCompletedSuccessfully(same(matching), eq(Optional.empty()));
    }


    @Test
    void testLaunchImminentShouldPerformAbortEvaluationWhenCalled() throws NoSuchMethodException {
        //given
        final Set<MissionHealthCheckEvaluator> matching = Collections.singleton(mock(MissionHealthCheckEvaluator.class));
        final LaunchSequenceTemplate underTest = spyWithMethodMatchers(null, matching);
        doReturn(Optional.empty())
                .when(underTest).evaluateLaunchAbort(same(matching), any(StageTimeStopwatch.class), any());

        //when
        underTest.launchImminent(this.getClass().getDeclaredMethod(METHOD_NAME));

        //then
        verify(underTest).evaluateLaunchAbort(same(matching), any(StageTimeStopwatch.class), any());
    }

    @Test
    void testLaunchFailureShouldFindEvaluatorsAndSuppressedExceptionsWhenCalled() throws NoSuchMethodException {
        //given
        final Set<MissionHealthCheckEvaluator> matching = Collections.singleton(mock(MissionHealthCheckEvaluator.class));
        final LaunchSequenceTemplate underTest = spyWithMethodMatchers(null, matching);

        //when
        underTest.launchFailure(this.getClass().getDeclaredMethod(METHOD_NAME), Optional.empty(), Optional.empty());

        //then
        verify(underTest).missionFailureDetected(same(matching), eq(Optional.empty()), eq(Optional.empty()), eq(Collections.emptySet()));
    }

    @Test
    void testLaunchSuccessShouldFindEvaluatorsWhenCalled() throws NoSuchMethodException {
        //given
        final Set<MissionHealthCheckEvaluator> matching = Collections.singleton(mock(MissionHealthCheckEvaluator.class));
        final LaunchSequenceTemplate underTest = spyWithMethodMatchers(null, matching);

        //when
        underTest.launchSuccess(this.getClass().getDeclaredMethod(METHOD_NAME), Optional.empty());

        //then
        verify(underTest).missionCompletedSuccessfully(same(matching), eq(Optional.empty()));
    }

    private LaunchSequenceTemplate spyWithMethodMatchers(final Set<MissionHealthCheckEvaluator> classMatcher,
                                                         final Set<MissionHealthCheckEvaluator> methodMatcher) {
        return spy(new LaunchSequenceTemplate(() -> fail(SHOULDN_T_BE_CALLED),
                c -> failIfNull(classMatcher),
                m -> failIfNull(methodMatcher)));
    }

    private Set<MissionHealthCheckEvaluator> failIfNull(final Set<MissionHealthCheckEvaluator> classMatcher) {
        if (classMatcher == null) {
            return fail(SHOULDN_T_BE_CALLED);
        }
        return classMatcher;
    }

    private void verifyOnlyIncrementFailedCalled(final StatisticsLogger logger) {
        verify(logger, never()).incrementSucceeded();
        verify(logger, never()).incrementSuppressed();
        verify(logger).incrementFailed();
        verify(logger, never()).incrementAborted();
    }

    private void verifyOnlyIncrementSuppressedCalled(final StatisticsLogger logger) {
        verify(logger, never()).incrementSucceeded();
        verify(logger).incrementSuppressed();
        verify(logger, never()).incrementFailed();
        verify(logger, never()).incrementAborted();
    }

    private void verifyOnlyIncrementSucceededCalled(final StatisticsLogger logger) {
        verify(logger).incrementSucceeded();
        verify(logger, never()).incrementSuppressed();
        verify(logger, never()).incrementFailed();
        verify(logger, never()).incrementAborted();
    }

    private void verifyOnlyIncrementAbortedCalled(final StatisticsLogger logger) {
        verify(logger, never()).incrementSuppressed();
        verify(logger, never()).incrementSucceeded();
        verify(logger, never()).incrementFailed();
        verify(logger).incrementAborted();
    }

    private Set<MissionHealthCheckEvaluator> addEvaluatorMockWithSpyLogger(final Set<MissionHealthCheckEvaluator> evaluators,
                                                                           final boolean aborting) {
        final MissionHealthCheckEvaluator evaluator = mock(MissionHealthCheckEvaluator.class);
        when(evaluator.countdownLogger()).thenReturn(spy(new StageStatisticsCollector()));
        when(evaluator.missionLogger()).thenReturn(spy(new StageStatisticsCollector()));
        when(evaluator.shouldAbort()).thenReturn(aborting);
        when(evaluator.shouldAbortCountdown()).thenReturn(aborting);
        evaluators.add(evaluator);
        return evaluators;
    }

    private Optional<StageTimeStopwatch> presentOptionalStopwatch() {
        return Optional.of(new StageTimeStopwatch(getClass()));
    }

    private LaunchSequenceTemplate underTestWithNullFunctions() {
        return new LaunchSequenceTemplate(() -> {
        }, c -> null, m -> null);
    }
}
