package com.github.nagyesta.abortmission.core.matcher.impl;

import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.Collection;
import java.util.Set;

import static com.github.nagyesta.abortmission.core.matcher.impl.MissionHealthCheckMatcherBuilder.builder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrMatcherTest extends BaseCombiningMatcherTest {

    @ParameterizedTest
    @MethodSource("inputProvider")
    void testMatchesRequiresOnlyOneOperandToMatchWhenCalled(final boolean matches, final Collection<String> operands) {
        //given
        final var builder = (MissionHealthCheckMatcherBuilder) builder();
        for (final var regex : operands) {
            builder.or(builder().classNamePattern(regex).build());
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
        assertThrows(IllegalArgumentException.class, () -> new OrMatcher(input));

        //then + exception
    }
}
