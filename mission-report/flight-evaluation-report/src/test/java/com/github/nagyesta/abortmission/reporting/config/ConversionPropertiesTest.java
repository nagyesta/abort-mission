package com.github.nagyesta.abortmission.reporting.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

class ConversionPropertiesTest {

    @Test
    void testConstructorShouldThrowExceptionWhenInputIsNull() {
        //given
        final var builder = ConversionProperties.builder()
                .output(new File("out.html"));

        //when
        Assertions.assertThrows(IllegalArgumentException.class, builder::build);

        //then + exception
    }

    @Test
    void testConstructorShouldThrowExceptionWhenOutputIsNull() {
        //given
        final var builder = ConversionProperties.builder()
                .input(new File("in.json"));

        //when
        Assertions.assertThrows(IllegalArgumentException.class, builder::build);

        //then + exception
    }
}
