package com.github.nagyesta.abortmission.reporting.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

class ConversionPropertiesTest {

    @Test
    void testConstructorShouldThrowExceptionWhenInputIsnull() {
        //given
        final ConversionProperties.ConversionPropertiesBuilder builder = ConversionProperties.builder()
                .output(new File("out.html"));

        //when
        //noinspection Convert2MethodRef
        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.build());

        //then + exception
    }

    @Test
    void testConstructorShouldThrowExceptionWhenOutputIsnull() {
        //given
        final ConversionProperties.ConversionPropertiesBuilder builder = ConversionProperties.builder()
                .input(new File("in.json"));

        //when
        Assertions.assertThrows(IllegalArgumentException.class, builder::build);

        //then + exception
    }
}
