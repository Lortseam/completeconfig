package me.lortseam.completeconfig.serialization;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import me.lortseam.completeconfig.data.Collection;

import java.lang.reflect.Type;

public class CollectionSerializer implements JsonSerializer<Collection> {

    public static final Type TYPE = new TypeToken<Collection>() {}.getType();

    @Override
    public JsonElement serialize(Collection src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        src.getCollections().forEach((key, collection) -> jsonObject.add(key, context.serialize(collection)));
        src.getEntries().forEach((key, entry) -> jsonObject.add(key, context.serialize(entry)));
        return jsonObject;
    }

}
