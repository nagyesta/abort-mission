package com.github.nagyesta.abortmission.testkit.vanilla;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FuelTankTest {

    private static final int SOME_FUEL = 42;
    private static final int AN_INVALID_AMOUNT_OF_FUEL = -42;
    private static final int A_LOT_OF_FUEL = 42000;

    @Test
    void testLoadShouldNotThrowExceptionsWhenCalledWithValidNumbers() {
        //given
        final FuelTank underTest = new FuelTank();

        //when
        underTest.load(SOME_FUEL);

        //then no exception
    }

    @Test
    void testLoadShouldThrowExceptionsWhenCalledWithNegativeNumbers() {
        //given
        final FuelTank underTest = new FuelTank();

        //when
        assertThrows(UnsupportedOperationException.class, () -> underTest.load(AN_INVALID_AMOUNT_OF_FUEL));

        //then exception
    }

    @Test
    void testLoadShouldThrowExceptionsWhenCalledWithLargeNumbers() {
        //given
        final FuelTank underTest = new FuelTank();

        //when
        assertThrows(IllegalStateException.class, () -> underTest.load(A_LOT_OF_FUEL));

        //then exception
    }
}
