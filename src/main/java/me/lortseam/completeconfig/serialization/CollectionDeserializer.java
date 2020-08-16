package me.lortseam.completeconfig.serialization;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import me.lortseam.completeconfig.collection.Collection;
import me.lortseam.completeconfig.collection.CollectionMap;
import me.lortseam.completeconfig.collection.EntryMap;
import me.lortseam.completeconfig.entry.Entry;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
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
        return new Collection(context.deserialize(collections, new TypeToken<CollectionMap>() {}.getType()), context.deserialize(entries, new TypeToken<EntryMap>() {}.getType()));
    }

}
