package com.github.nagyesta.abortmission.core.telemetry;

import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Stream;

class LocalDateTimeAdapterTest {

    private static final LocalDateTime EXPECTED_FROM = LocalDateTime
            .of(2000, 12, 31, 23, 59, 59, 0);
    private static final LocalDateTime EXPECTED_TO = LocalDateTime
            .of(2001, 1, 1, 0, 4, 59, 0);

    @SuppressWarnings("checkstyle:MagicNumber")
    public static Stream<Arguments> validWriteSource() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, "null"))
                .add(Arguments.of(LocalDateTime.of(2022, 1, 2, 3, 4, 5, 6),
                        "1641092645"))
                .add(Arguments.of(LocalDateTime.of(2000, 12, 31, 23, 59, 59, 999000000),
                        "978307199"))
                .build();
    }

    @ParameterizedTest
    @MethodSource("validWriteSource")
    void testWriteShouldProduceValidOutputWhenCalled(
            final LocalDateTime input,
            final String expected) {
        //given
        final var underTest = new LocalDateTimeAdapter();
        final var gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, underTest).create();

        //when
        final var actual = gson.toJson(input);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testReadShouldReadSingleObjectWhenValidDataIsRead() {
        //given
        final var resourceName = "/json/date/local-date-time-valid.json";
        final var underTest = new LocalDateTimeAdapter();
        final var gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, underTest).create();

        try (var stream = getClass().getResourceAsStream(resourceName);
             var reader = new InputStreamReader(Objects.requireNonNull(stream))
        ) {
            //when
            final var actual = gson.fromJson(reader, LocalDateTime.class);

            //then
            Assertions.assertEquals(EXPECTED_FROM, actual);
        } catch (final IOException e) {
            Assertions.fail(e.getMessage(), e);
        }
    }

    @Test
    void testReadShouldReadComplexObjectWhenValidDataIsRead() {
        //given
        final var resourceName = "/json/date/local-date-time-valid-complex.json";
        final var underTest = new LocalDateTimeAdapter();
        final var gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, underTest).create();

        try (var stream = getClass().getResourceAsStream(resourceName);
             var reader = new InputStreamReader(Objects.requireNonNull(stream))
        ) {
            //when
            final var actual = gson.fromJson(reader, FromTo.class);

            //then
            Assertions.assertEquals(EXPECTED_FROM, actual.getFrom());
            Assertions.assertEquals(EXPECTED_TO, actual.getTo());
        } catch (final IOException e) {
            Assertions.fail(e.getMessage(), e);
        }
    }

    @Test
    void testReadShouldReadComplexObjectWhenSomeFieldsAreNull() {
        //given
        final var resourceName = "/json/date/local-date-time-valid-complex-null.json";
        final var underTest = new LocalDateTimeAdapter();
        final var gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, underTest).create();

        try (var stream = getClass().getResourceAsStream(resourceName);
             var reader = new InputStreamReader(Objects.requireNonNull(stream))
        ) {
            //when
            final var actual = gson.fromJson(reader, FromTo.class);

            //then
            Assertions.assertEquals(EXPECTED_FROM, actual.getFrom());
            Assertions.assertNull(actual.getTo());
        } catch (final IOException e) {
            Assertions.fail(e.getMessage(), e);
        }
    }
}
