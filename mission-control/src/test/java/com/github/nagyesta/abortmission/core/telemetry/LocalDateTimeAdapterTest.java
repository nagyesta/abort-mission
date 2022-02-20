package com.github.nagyesta.abortmission.core.telemetry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

class LocalDateTimeAdapterTest {

    public static Stream<Arguments> invalidReadProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of("/json/date/local-date-time-invalid-missing-date.json"))
                .add(Arguments.of("/json/date/local-date-time-invalid-missing-time.json"))
                .add(Arguments.of("/json/date/local-date-time-invalid-missing-year.json"))
                .add(Arguments.of("/json/date/local-date-time-invalid-missing-month.json"))
                .add(Arguments.of("/json/date/local-date-time-invalid-missing-day.json"))
                .add(Arguments.of("/json/date/local-date-time-invalid-missing-hour.json"))
                .add(Arguments.of("/json/date/local-date-time-invalid-missing-minute.json"))
                .add(Arguments.of("/json/date/local-date-time-invalid-missing-second.json"))
                .add(Arguments.of("/json/date/local-date-time-invalid-missing-nano.json"))
                .build();
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    public static Stream<Arguments> validWriteSource() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, "null"))
                .add(Arguments.of(LocalDateTime.of(2022, 1, 2, 3, 4, 5, 6),
                        "{\"date\":{\"year\":2022,\"month\":1,\"day\":2},"
                                + "\"time\":{\"hour\":3,\"minute\":4,\"second\":5,\"nano\":6}}"))
                .add(Arguments.of(LocalDateTime.of(2000, 12, 31, 23, 59, 59, 999000000),
                        "{\"date\":{\"year\":2000,\"month\":12,\"day\":31},"
                                + "\"time\":{\"hour\":23,\"minute\":59,\"second\":59,\"nano\":999000000}}"))
                .build();
    }

    @ParameterizedTest
    @MethodSource("validWriteSource")
    void testWriteShouldProduceValidOutputWhenCalled(final LocalDateTime input, final String expected) {
        //given
        final LocalDateTimeAdapter underTest = new LocalDateTimeAdapter();
        final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, underTest).create();

        //when
        final String actual = gson.toJson(input);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testReadShouldReadSingleObjectWhenValidDataIsRead() {
        //given
        final String resourceName = "/json/date/local-date-time-valid.json";
        final LocalDateTimeAdapter underTest = new LocalDateTimeAdapter();
        final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, underTest).create();

        try (InputStream stream = getClass().getResourceAsStream(resourceName);
             InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(stream))
        ) {
            //when
            final LocalDateTime actual = gson.fromJson(reader, LocalDateTime.class);

            //then
            final LocalDateTime expected = LocalDateTime.of(2022, 2, 20, 13, 49, 10, 862000000);
            Assertions.assertEquals(expected, actual);
        } catch (final IOException e) {
            Assertions.fail(e.getMessage(), e);
        }
    }

    @Test
    void testReadShouldReadComplexObjectWhenValidDataIsRead() {
        //given
        final String resourceName = "/json/date/local-date-time-valid-complex.json";
        final LocalDateTimeAdapter underTest = new LocalDateTimeAdapter();
        final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, underTest).create();

        try (InputStream stream = getClass().getResourceAsStream(resourceName);
             InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(stream))
        ) {
            //when
            final Map<String, LocalDateTime> map = new HashMap<>();
            final FromTo actual = gson.fromJson(reader, FromTo.class);

            //then
            final LocalDateTime expectedFrom = LocalDateTime.of(2022, 2, 20, 13, 49, 10, 862000000);
            final LocalDateTime expectedTo = LocalDateTime.of(2022, 3, 10, 11, 11, 50, 100000000);
            Assertions.assertEquals(expectedFrom, actual.getFrom());
            Assertions.assertEquals(expectedTo, actual.getTo());
        } catch (final IOException e) {
            Assertions.fail(e.getMessage(), e);
        }
    }

    @ParameterizedTest
    @MethodSource("invalidReadProvider")
    void testReadShouldThrowExceptionWhenInvalidDataIsRead(final String resourceName) {
        //given
        final LocalDateTimeAdapter underTest = new LocalDateTimeAdapter();
        final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, underTest).create();

        try (InputStream stream = getClass().getResourceAsStream(resourceName);
             InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(stream))
        ) {
            //when
            Assertions.assertThrows(IllegalArgumentException.class, () -> gson.fromJson(reader, LocalDateTime.class));

            //then exception
        } catch (final IOException e) {
            Assertions.fail(e.getMessage(), e);
        }
    }
}
