package com.github.nagyesta.abortmission.core.matcher.impl;

import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class AbstractMatcherTest {

    protected void testEqualsAndHashCodeShouldBehaveInSyncWhenCalled(final MissionHealthCheckMatcher a,
                                                                     final MissionHealthCheckMatcher b,
                                                                     final boolean expected) {
        //given

        //when
        final var actual = a.equals(b);
        final var actualHashA = Objects.hashCode(a);
        final var actualHashB = Objects.hashCode(b);

        //then
        assertEquals(expected, actual);
        assertEquals(expected, actualHashA == actualHashB);
    }
}
