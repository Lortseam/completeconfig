package me.lortseam.completeconfig;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import me.lortseam.completeconfig.api.ConfigCategory;
import me.lortseam.completeconfig.data.CollectionMap;
import me.lortseam.completeconfig.serialization.CollectionMapDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class Config extends CollectionMap {

    private static final Logger LOGGER = LogManager.getLogger();

    Config(String modID, List<ConfigCategory> topLevelCategories, JsonElement json) {
        super("config." + modID);
        for (ConfigCategory category : topLevelCategories) {
            if (!fill(modTranslationKey, category)) {
                continue;
            }
            try {
                new GsonBuilder()
                        .registerTypeAdapter(CollectionMapDeserializer.TYPE, new CollectionMapDeserializer(this, category.getConfigCategoryID()))
                        .create()
                        .fromJson(json, CollectionMapDeserializer.TYPE);
            } catch (JsonSyntaxException e) {
                LOGGER.warn("[CompleteConfig] An error occurred while trying to load the config for category " + category.getClass());
            }
        }
    }

    public String getModTranslationKey() {
        return modTranslationKey;
    }

}
