package com.github.nagyesta.abortmission.core;

import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;
import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AbortMissionCommandOpsTest {

    private static final String AFTER_CONTEXT_WAS_INITIALIZED = "AfterContextWasInitialized";

    @ParameterizedTest
    @ValueSource(strings = {" ", "       "})
    @NullAndEmptySource
    void testNamedShouldNotAllowAccessToTheSharedContextWhenCalled(final String name) {
        //given
        final Class<? extends Exception> expectedType;
        if (name == null) {
            expectedType = NullPointerException.class;
        } else {
            expectedType = IllegalArgumentException.class;
        }

        //when
        Assertions.assertThrows(expectedType,
                () -> AbortMissionCommandOps.named(name));

        //then exception
    }

    @Test
    void testRegisterHealthCheckShouldThrowExceptionWhenCalledAfterContextWasInitialized() {
        //given
        AbortMissionCommandOps.newInstance().finalizeSetup(AFTER_CONTEXT_WAS_INITIALIZED);
        final var ops = AbortMissionCommandOps.named(AFTER_CONTEXT_WAS_INITIALIZED);
        final var evaluator = mock(MissionHealthCheckEvaluator.class);

        //when
        Assertions.assertThrows(IllegalStateException.class, () -> ops.registerHealthCheck(evaluator));

        //then exception
    }

    @Test
    void testRegisterHealthCheckShouldThrowExceptionWhenCalledTwiceWithTheSame() {
        //given
        final var underTest = AbortMissionCommandOps.newInstance();
        final var matcher = mock(MissionHealthCheckMatcher.class);
        final var evaluator = mock(MissionHealthCheckEvaluator.class);
        when(evaluator.getMatcher()).thenReturn(matcher);

        //when
        underTest.registerHealthCheck(evaluator);
        Assertions.assertThrows(IllegalStateException.class, () -> underTest.registerHealthCheck(evaluator));

        //then exception
    }
}
