package com.github.nagyesta.abortmission.strongback.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class StrongbackExceptionTest {

    @Test
    void testThrowWithOnlyMessageShouldWorkWhenCalled() {
        //given

        //when
        Assertions.assertThrows(StrongbackException.class, () -> {
            throw new StrongbackException("");
        });

        //then + exception
    }

    @Test
    void testThrowWithMessageAndCauseShouldWorkWhenCalled() {
        //given

        //when
        Assertions.assertThrows(StrongbackException.class, () -> {
            throw new StrongbackException("", new IllegalArgumentException());
        });

        //then + exception
    }

    @Test
    void testThrowWithOnlyCauseShouldWorkWhenCalled() {
        //given

        //when
        Assertions.assertThrows(StrongbackException.class, () -> {
            throw new StrongbackException(new IllegalArgumentException());
        });

        //then + exception
    }
}
