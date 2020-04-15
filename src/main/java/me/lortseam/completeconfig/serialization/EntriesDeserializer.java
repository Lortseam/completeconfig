package me.lortseam.completeconfig.serialization;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import me.lortseam.completeconfig.entry.Entry;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;

public class EntriesDeserializer implements JsonDeserializer<LinkedHashMap<String, Entry>> {

    public static final Type TYPE = new TypeToken<LinkedHashMap<String, Entry>>() {}.getType();

    private final LinkedHashMap<String, Entry> configMap;

    public EntriesDeserializer(LinkedHashMap<String, Entry> configMap) {
        this.configMap = configMap;
    }

    @Override
    public LinkedHashMap<String, Entry> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        LinkedHashMap<String, JsonElement> map = new Gson().fromJson(json, new TypeToken<LinkedHashMap<String, JsonElement>>() {}.getType());
        map.forEach((entryID, element) -> {
            Entry<?> entry = configMap.get(entryID);
            if (entry == null) {
                return;
            }
            new GsonBuilder()
                    .registerTypeAdapter(EntryDeserializer.TYPE, new EntryDeserializer<>(entry))
                    .create()
                    .fromJson(element, new TypeToken<Entry<?>>() {}.getType());
        });
        return null;
    }

}