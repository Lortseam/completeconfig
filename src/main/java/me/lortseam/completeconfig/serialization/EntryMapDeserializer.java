package me.lortseam.completeconfig.serialization;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import me.lortseam.completeconfig.collection.EntryMap;
import me.lortseam.completeconfig.entry.Entry;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;

@RequiredArgsConstructor
public class EntryMapDeserializer implements JsonDeserializer<EntryMap> {

    public static final Type TYPE = new TypeToken<EntryMap>() {}.getType();

    private final EntryMap configMap;

    @Override
    public EntryMap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
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