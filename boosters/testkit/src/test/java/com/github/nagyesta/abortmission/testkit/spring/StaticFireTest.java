package com.github.nagyesta.abortmission.testkit.spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StaticFireTest {

    @Test
    void testContextCreationFailsWhenSideBoosterCreated() {
        //given
        final ConfigurableApplicationContext context = SpringApplication.run(StaticFire.class);

        //when
        assertThrows(BeanCreationException.class, () -> context.getBean("sideBooster"));

        //then exception
    }


    @Test
    void testContextCreationSucceedsAndCenterCoreIsOnFireWhenCenterCoreCreated() {
        //given
        final ConfigurableApplicationContext context = SpringApplication.run(StaticFire.class);

        //when
        final Booster centerCore = context.getBean("centerCore", Booster.class);

        //then
        assertTrue(centerCore.isOnFire());
    }
}
