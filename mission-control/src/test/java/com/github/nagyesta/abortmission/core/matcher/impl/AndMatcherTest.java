package com.github.nagyesta.abortmission.core.matcher.impl;

import com.github.nagyesta.abortmission.core.MissionControl;
import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

import static com.github.nagyesta.abortmission.core.matcher.impl.MissionHealthCheckMatcherBuilder.builder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AndMatcherTest extends BaseCombiningMatcherTest {

    public static final MissionHealthCheckMatcher ANY_TEST_CLASS = MissionControl.matcher()
            .and(ANY_CLASS_MATCHER)
            .andAtLast(TEST_CLASS_MATCHER)
            .build();

    private static Stream<Arguments> equalsAndHashCodeProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(
                        MissionControl.matcher()
                                .and(ANY_CLASS_MATCHER)
                                .andAtLast(ANY_CLASS_MATCHER)
                                .build(),
                        MissionControl.matcher()
                                .and(MissionControl.matcher().classNamePattern(".*Test").build())
                                .andAtLast(ANY_CLASS_MATCHER)
                                .build(),
                        false
                )).add(Arguments.of(
                        ANY_TEST_CLASS,
                        MissionControl.matcher()
                                .and(TEST_CLASS_MATCHER)
                                .andAtLast(ANY_CLASS_MATCHER)
                                .build(),
                        true
                )).add(Arguments.of(
                        ANY_TEST_CLASS,
                        TEST_CLASS_MATCHER,
                        false
                )).add(Arguments.of(
                        ANY_TEST_CLASS,
                        ANY_TEST_CLASS,
                        true
                ))
                .build();
    }

    @ParameterizedTest
    @MethodSource("inputProvider")
    void testMatchesRequiresAllOperandsToMatchWhenCalled(final boolean matches, final Collection<String> operands) {
        //given
        final var builder = (MissionHealthCheckMatcherBuilder) builder();
        for (final var regex : operands) {
            builder.and(builder().classNamePattern(regex).build());
        }
        final var underTest = builder.build();

        //when
        final var actual = underTest.matches(this.getClass());

        //then
        assertEquals(matches, actual);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @MethodSource("nullItemProvider")
    void testConstructorShouldThrowExceptionWhenCalledWithNulls(final Set<MissionHealthCheckMatcher> input) {
        //given

        //when
        assertThrows(IllegalArgumentException.class, () -> new AndMatcher(input));

        //then + exception
    }

    @ParameterizedTest
    @MethodSource("equalsAndHashCodeProvider")
    @Override
    protected void testEqualsAndHashCodeShouldBehaveInSyncWhenCalled(final MissionHealthCheckMatcher a,
                                                                     final MissionHealthCheckMatcher b,
                                                                     final boolean expected) {
        super.testEqualsAndHashCodeShouldBehaveInSyncWhenCalled(a, b, expected);
    }
}
