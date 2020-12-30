package me.lortseam.completeconfig.serialization;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import me.lortseam.completeconfig.data.Collection;
import me.lortseam.completeconfig.data.CollectionMap;
import me.lortseam.completeconfig.data.EntryMap;

import java.lang.reflect.Type;
import java.util.Map;

public class CollectionDeserializer implements JsonDeserializer<Collection> {

    public static final Type TYPE = new TypeToken<Collection>() {}.getType();

    @Override
    public Collection deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject collections = new JsonObject();
        JsonObject entries = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
            if (entry.getValue().isJsonObject()) {
                collections.add(entry.getKey(), entry.getValue());
            } else {
                entries.add(entry.getKey(), entry.getValue());
            }
        }
        context.deserialize(collections, new TypeToken<CollectionMap>() {}.getType());
        context.deserialize(entries, new TypeToken<EntryMap>() {}.getType());
        return null;
    }

}
