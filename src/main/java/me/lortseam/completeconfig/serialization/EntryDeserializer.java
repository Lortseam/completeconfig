package me.lortseam.completeconfig.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import me.lortseam.completeconfig.entry.Entry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Type;

public class EntryDeserializer<T> implements JsonDeserializer<Entry<T>> {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final Type TYPE = new TypeToken<Entry<?>>() {}.getType();

    private final Entry<T> configEntry;

    public EntryDeserializer(Entry<T> configEntry) {
        this.configEntry = configEntry;
    }

    @Override
    public Entry<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            T value = context.deserialize(json, configEntry.getType());
            configEntry.setValue(value);
        } catch (JsonParseException e) {
            LOGGER.warn("[CompleteConfig] An error occurred while trying to load the config entry's value of field " + configEntry.getField() + ": " + e.getMessage());
        }
        return configEntry;
    }

}
