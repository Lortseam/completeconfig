package me.lortseam.completeconfig.serialization;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import me.lortseam.completeconfig.data.Entry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class EntryDeserializer<T> extends JsonDeserializer<Entry<T>> {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final Class<Entry> TYPE = Entry.class;

    private final Entry<T> configEntry;

    public EntryDeserializer(Entry<T> configEntry) {
        this.configEntry = configEntry;
    }

    @Override
    public Entry<T> deserialize(JsonParser parser, DeserializationContext context) {
        try {
            T value = context.readValue(parser, configEntry.getType());
            configEntry.setValue(value);
        } catch (IOException e) {
            LOGGER.warn("[CompleteConfig] An error occurred while trying to load the config entry's value of field " + configEntry.getField() + ": " + e.getMessage());
        }
        return configEntry;
    }

}
