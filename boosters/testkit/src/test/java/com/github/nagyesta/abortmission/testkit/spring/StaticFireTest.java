package com.github.nagyesta.abortmission.testkit.spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.SpringApplication;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StaticFireTest {

    @Test
    void testContextCreationFailsWhenSideBoosterCreated() {
        //given
        final var context = SpringApplication.run(StaticFire.class);

        //when
        assertThrows(BeanCreationException.class, () -> context.getBean("sideBooster"));

        //then exception
    }


    @Test
    void testContextCreationSucceedsAndCenterCoreIsOnFireWhenCenterCoreCreated() {
        //given
        final var context = SpringApplication.run(StaticFire.class);

        //when
        final var centerCore = context.getBean("centerCore", Booster.class);

        //then
        assertTrue(centerCore.isOnFire());
    }
}
