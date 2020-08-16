package me.lortseam.completeconfig;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.lortseam.completeconfig.api.ConfigCategory;
import me.lortseam.completeconfig.collection.CollectionMap;
import me.lortseam.completeconfig.serialization.CollectionMapDeserializer;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class Config extends CollectionMap {

    private final JsonElement json;

    void registerTopLevelCategory(ConfigCategory category) {
        fill(category);
        new GsonBuilder()
                .registerTypeAdapter(CollectionMapDeserializer.TYPE, new CollectionMapDeserializer(this, category.getConfigCategoryID()))
                .create()
                .fromJson(json, CollectionMapDeserializer.TYPE);
    }

}
