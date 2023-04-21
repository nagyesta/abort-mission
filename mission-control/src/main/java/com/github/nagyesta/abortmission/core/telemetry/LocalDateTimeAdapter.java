package com.github.nagyesta.abortmission.core.telemetry;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    @Override
    public void write(final JsonWriter out, final LocalDateTime value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.toEpochSecond(ZoneOffset.UTC));
        }
    }

    @Override
    public LocalDateTime read(final JsonReader in) throws IOException {
        if (in.peek().equals(JsonToken.NULL)) {
            in.nextNull();
            return null;
        } else {
            return LocalDateTime.ofEpochSecond(in.nextLong(), 0, ZoneOffset.UTC);
        }
    }

}
