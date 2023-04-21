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
        final AbortMissionGlobalConfiguration first = AbortMissionGlobalConfiguration.shared();
        final AbortMissionGlobalConfiguration second = AbortMissionGlobalConfiguration.shared();

        //then
        assertSame(first, second);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    void testGetStackTraceDepthShouldReturnTheDefaultDepthWhenCalledBeforeSettingThenTheSetValueAfter() {
        //given
        final int setDepth = 42;

        //when
        final int initialDepth = AbortMissionGlobalConfiguration.shared().getStackTraceDepth();
        AbortMissionGlobalConfiguration.shared().setStackTraceDepth(setDepth);
        final int updatedDepth = AbortMissionGlobalConfiguration.shared().getStackTraceDepth();

        //then
        assertEquals(AbortMissionGlobalConfiguration.DEFAULT_STACK_TRACE_DEPTH, initialDepth);
        assertEquals(setDepth, updatedDepth);
    }

    @Test
    void testGetStackTraceFilterShouldReturnTheDefaultFilterWhenCalledBeforeSettingThenTheSetValueAfter() {
        //given
        final Predicate<StackTraceElement> setFilter = StackTraceElement::isNativeMethod;

        //when
        final Predicate<StackTraceElement> initialFilter = AbortMissionGlobalConfiguration.shared().getStackTraceFilter();
        AbortMissionGlobalConfiguration.shared().setStackTraceFilter(setFilter);
        final Predicate<StackTraceElement> updatedFilter = AbortMissionGlobalConfiguration.shared().getStackTraceFilter();

        //then
        assertSame(AbortMissionGlobalConfiguration.DEFAULT_STACK_TRACE_FILTER, initialFilter);
        assertSame(setFilter, updatedFilter);
    }
}
