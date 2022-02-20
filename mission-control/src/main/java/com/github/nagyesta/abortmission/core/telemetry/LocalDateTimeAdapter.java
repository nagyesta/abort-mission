package com.github.nagyesta.abortmission.core.telemetry;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    private static final String DATE = "date";
    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY = "day";
    private static final String TIME = "time";
    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";
    private static final String SECOND = "second";
    private static final String NANO = "nano";

    @Override
    public void write(final JsonWriter out, final LocalDateTime value) throws IOException {
        out.beginObject()
                .name(DATE).beginObject()
                .name(YEAR).value(value.getYear())
                .name(MONTH).value(value.getMonthValue())
                .name(DAY).value(value.getDayOfMonth())
                .endObject()
                .name(TIME).beginObject()
                .name(HOUR).value(value.getHour())
                .name(MINUTE).value(value.getMinute())
                .name(SECOND).value(value.getSecond())
                .name(NANO).value(value.getNano())
                .endObject()
                .endObject();
    }

    @Override
    public LocalDateTime read(final JsonReader in) throws IOException {
        final Map<String, Map<String, Integer>> map = new TreeMap<>();
        in.beginObject();
        while (in.hasNext()) {
            readDateOrTimePart(in, map, in.nextName());
        }
        in.endObject();
        validate(map);
        return LocalDateTime.of(
                map.get(DATE).get(YEAR),
                map.get(DATE).get(MONTH),
                map.get(DATE).get(DAY),
                map.get(TIME).get(HOUR),
                map.get(TIME).get(MINUTE),
                map.get(TIME).get(SECOND),
                map.get(TIME).get(NANO)
        );
    }

    private void validate(final Map<String, Map<String, Integer>> map) {
        final Map<String, Integer> date = assertNotNull(map.get(DATE), "Date component not found.");
        assertNotNull(date.get(YEAR), "Year component not found.");
        assertNotNull(date.get(MONTH), "Month component not found.");
        assertNotNull(date.get(DAY), "Day component not found.");
        final Map<String, Integer> time = assertNotNull(map.get(TIME), "Time component not found.");
        assertNotNull(time.get(HOUR), "Hour component not found.");
        assertNotNull(time.get(MINUTE), "Minute component not found.");
        assertNotNull(time.get(SECOND), "Second component not found.");
        final Integer value = time.get(NANO);
        final String message = "Nano component not found.";
        assertNotNull(value, message);
    }

    private <T> T assertNotNull(final T value, final String message) {
        return Optional.ofNullable(value)
                .orElseThrow(() -> new IllegalArgumentException(message));
    }

    private void readDateOrTimePart(final JsonReader in,
                                    final Map<String, Map<String, Integer>> map,
                                    final String name) throws IOException {
        final Map<String, Integer> innerMap = map.computeIfAbsent(name, k -> new LinkedHashMap<>());
        in.beginObject();
        while (in.hasNext()) {
            final String field = in.nextName();
            innerMap.put(field, in.nextInt());
        }
        in.endObject();
    }
}
