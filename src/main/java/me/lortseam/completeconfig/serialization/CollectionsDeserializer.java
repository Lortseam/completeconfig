package me.lortseam.completeconfig.serialization;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import me.lortseam.completeconfig.collection.Collection;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;

public class CollectionsDeserializer implements JsonDeserializer<LinkedHashMap<String, Collection>> {

    public static final Type TYPE = new TypeToken<LinkedHashMap<String, Collection>>() {}.getType();

    private final LinkedHashMap<String, Collection> configMap;
    private final String collectionID;

    public CollectionsDeserializer(LinkedHashMap<String, Collection> configMap, String collectionID) {
        this.configMap = configMap;
        this.collectionID = collectionID;
    }

    private CollectionsDeserializer(LinkedHashMap<String, Collection> configMap) {
        this(configMap, null);
    }

    @Override
    public LinkedHashMap<String, Collection> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        LinkedHashMap<String, JsonElement> map = new Gson().fromJson(json, new TypeToken<LinkedHashMap<String, JsonElement>>() {}.getType());
        if (collectionID == null) {
            map.forEach(this::deserialize);
        } else {
            deserialize(collectionID, map.get(collectionID));
        }
        return null;
    }

    private void deserialize(String collectionID, JsonElement element) {
        Collection collection = configMap.get(collectionID);
        if (collection == null) {
            return;
        }
        new GsonBuilder()
                .registerTypeAdapter(CollectionsDeserializer.TYPE, new CollectionsDeserializer(collection.getCollections()))
                .registerTypeAdapter(CollectionDeserializer.TYPE, new CollectionDeserializer())
                .registerTypeAdapter(EntriesDeserializer.TYPE, new EntriesDeserializer(collection.getEntries()))
                .create()
                .fromJson(element, Collection.class);
    }

}
