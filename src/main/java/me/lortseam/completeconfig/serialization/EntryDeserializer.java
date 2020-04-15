package me.lortseam.completeconfig.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import me.lortseam.completeconfig.entry.Entry;

import java.lang.reflect.Type;

public class EntryDeserializer<T> implements JsonDeserializer<Entry<T>> {

    public static final Type TYPE = new TypeToken<Entry<?>>() {}.getType();

    private final Entry<T> configEntry;

    public EntryDeserializer(Entry<T> configEntry) {
        this.configEntry = configEntry;
    }

    @Override
    public Entry<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        configEntry.setValue(context.deserialize(json, configEntry.getType()));
        return configEntry;
    }

}
