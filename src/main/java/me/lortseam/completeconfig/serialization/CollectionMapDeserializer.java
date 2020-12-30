package me.lortseam.completeconfig.serialization;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import me.lortseam.completeconfig.data.Collection;
import me.lortseam.completeconfig.data.CollectionMap;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

public class CollectionMapDeserializer implements JsonDeserializer<CollectionMap> {

    private static final Gson GSON = new Gson();
    public static final Type TYPE = new TypeToken<CollectionMap>() {}.getType();

    private final Map<String, Collection> configMap;
    private final String collectionID;

    public CollectionMapDeserializer(Map<String, Collection> configMap, String collectionID) {
        this.configMap = configMap;
        this.collectionID = collectionID;
    }

    private CollectionMapDeserializer(Map<String, Collection> configMap) {
        this(configMap, null);
    }

    @Override
    public CollectionMap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        LinkedHashMap<String, JsonElement> map = GSON.fromJson(json, new TypeToken<LinkedHashMap<String, JsonElement>>() {}.getType());
        if (collectionID == null) {
            map.forEach(this::deserializeCollection);
        } else {
            deserializeCollection(collectionID, map.get(collectionID));
        }
        return null;
    }

    private void deserializeCollection(String collectionID, JsonElement element) {
        Collection collection = configMap.get(collectionID);
        if (collection == null) {
            return;
        }
        new GsonBuilder()
                .registerTypeAdapter(CollectionDeserializer.TYPE, new CollectionDeserializer())
                .registerTypeAdapter(CollectionMapDeserializer.TYPE, new CollectionMapDeserializer(collection.getCollections()))
                .registerTypeAdapter(EntryMapDeserializer.TYPE, new EntryMapDeserializer(collection.getEntries()))
                .create()
                .fromJson(element, Collection.class);
    }

}
