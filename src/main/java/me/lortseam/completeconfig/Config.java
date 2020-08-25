package me.lortseam.completeconfig;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import me.lortseam.completeconfig.api.ConfigCategory;
import me.lortseam.completeconfig.collection.CollectionMap;
import me.lortseam.completeconfig.serialization.CollectionMapDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Config extends CollectionMap {

    private static final Logger LOGGER = LogManager.getLogger();

    private final JsonElement json;

    Config(String modID, JsonElement json) {
        super("config." + modID);
        this.json = json;
    }

    void registerTopLevelCategory(ConfigCategory category) {
        fill(modTranslationKey, category);
        try {
            new GsonBuilder()
                    .registerTypeAdapter(CollectionMapDeserializer.TYPE, new CollectionMapDeserializer(this, category.getConfigCategoryID()))
                    .create()
                    .fromJson(json, CollectionMapDeserializer.TYPE);
        } catch (JsonSyntaxException e) {
            LOGGER.warn("An error occurred while trying to load the config for category " + category.getClass());
        }
    }

    public String getModTranslationKey() {
        return modTranslationKey;
    }

}
