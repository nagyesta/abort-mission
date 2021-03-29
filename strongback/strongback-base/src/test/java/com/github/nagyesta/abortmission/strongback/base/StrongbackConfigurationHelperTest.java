package com.github.nagyesta.abortmission.strongback.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class StrongbackConfigurationHelperTest {

    @Test
    void testOptionalPortValueShouldReturnEmptyWhenTheSystemPropertyIsNotProvided() {
        //given

        //when
        final Optional<Integer> actual = StrongbackConfigurationHelper.optionalPortValue();

        //then
        Assertions.assertFalse(actual.isPresent());
    }

    @Test
    void testUseExternalServerShouldReturnDefaultValueWhenTheSystemPropertyIsNotProvided() {
        //given

        //when
        final boolean actual = StrongbackConfigurationHelper.useExternalServer();

        //then
        Assertions.assertFalse(actual);
    }

    @Test
    void testOptionalServerPasswordShouldReturnEmptyValueWhenTheSystemPropertyIsNotProvided() {
        //given

        //when
        final Optional<String> actual = StrongbackConfigurationHelper.optionalServerPassword();

        //then
        Assertions.assertFalse(actual.isPresent());
    }

    @Test
    void testSafeParseIntTypeShouldReturnParsedValueWhenValidInputProvided() {
        //given

        //when
        final Optional<Integer> actual = StrongbackConfigurationHelper.safeParseIntType("1");

        //then
        Assertions.assertTrue(actual.isPresent());
        Assertions.assertEquals(1, actual.get());
    }

    @Test
    void testSafeParseBooleanTypeShouldReturnParsedValueWhenValidInputProvided() {
        //given

        //when
        final boolean actual = StrongbackConfigurationHelper.safeParseBooleanType("true");

        //then
        Assertions.assertTrue(actual);
    }
}
