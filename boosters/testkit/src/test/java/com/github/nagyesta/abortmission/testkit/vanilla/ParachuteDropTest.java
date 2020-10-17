package com.github.nagyesta.abortmission.testkit.vanilla;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("checkstyle:MagicNumber")
class ParachuteDropTest {

    private final ParachuteDrop underTest = new ParachuteDrop();

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void testCanOpenParachuteShouldSayTrueWhenCalledWithValidIndex(final int parachuteIndex) {
        //given

        //when
        final boolean actual = underTest.canOpenParachute(parachuteIndex);

        //then
        assertTrue(actual);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 3, 42})
    void testCanOpenParachuteShouldSayFalseWhenCalledWithInvalidIndex(final int parachuteIndex) {
        //given

        //when
        final boolean actual = underTest.canOpenParachute(parachuteIndex);

        //then
        assertFalse(actual);
    }
}
