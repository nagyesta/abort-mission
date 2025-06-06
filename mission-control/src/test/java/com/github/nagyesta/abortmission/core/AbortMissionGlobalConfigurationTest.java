package com.github.nagyesta.abortmission.core;

import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class AbortMissionGlobalConfigurationTest {

    @Test
    void testSharedShouldReturnTheSameInstanceEveryTimeWhenCalledRepeatably() {
        //given

        //when
        final var first = AbortMissionGlobalConfiguration.shared();
        final var second = AbortMissionGlobalConfiguration.shared();

        //then
        assertSame(first, second);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    void testGetStackTraceDepthShouldReturnTheDefaultDepthWhenCalledBeforeSettingThenTheSetValueAfter() {
        //given
        final var setDepth = 42;

        //when
        final var initialDepth = AbortMissionGlobalConfiguration.shared().getStackTraceDepth();
        AbortMissionGlobalConfiguration.shared().setStackTraceDepth(setDepth);
        final var updatedDepth = AbortMissionGlobalConfiguration.shared().getStackTraceDepth();

        //then
        assertEquals(AbortMissionGlobalConfiguration.DEFAULT_STACK_TRACE_DEPTH, initialDepth);
        assertEquals(setDepth, updatedDepth);
    }

    @Test
    void testGetStackTraceFilterShouldReturnTheDefaultFilterWhenCalledBeforeSettingThenTheSetValueAfter() {
        //given
        final Predicate<StackTraceElement> setFilter = StackTraceElement::isNativeMethod;

        //when
        final var initialFilter = AbortMissionGlobalConfiguration.shared().getStackTraceFilter();
        AbortMissionGlobalConfiguration.shared().setStackTraceFilter(setFilter);
        final var updatedFilter = AbortMissionGlobalConfiguration.shared().getStackTraceFilter();

        //then
        assertSame(AbortMissionGlobalConfiguration.DEFAULT_STACK_TRACE_FILTER, initialFilter);
        assertSame(setFilter, updatedFilter);
    }
}
