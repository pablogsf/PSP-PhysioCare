package com.matias.physiocarepsp.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Custom Gson adapter for serializing and deserializing LocalDate objects.
 */
public class LocalDateAdapter extends TypeAdapter<LocalDate> {

    /**
     * Serializes a LocalDate object to its JSON representation.
     * @param out The JsonWriter to write the JSON data.
     * @param value The LocalDate object to serialize.
     * @throws IOException If an I/O error occurs during writing.
     */
    @Override
    public void write(JsonWriter out, LocalDate value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.toString());
        }
    }

    /**
     * Deserializes a JSON string to a LocalDate object.
     * @param in The JsonReader to read the JSON data.
     * @return The deserialized LocalDate object, or null if the input is empty or null.
     * @throws IOException If an I/O error occurs during reading.
     */
    @Override
    public LocalDate read(JsonReader in) throws IOException {
        String dateTimeString = in.nextString();
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return null;
        }
        return OffsetDateTime.parse(dateTimeString, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDate();
    }
}