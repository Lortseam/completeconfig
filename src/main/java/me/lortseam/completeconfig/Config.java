package me.lortseam.completeconfig;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import me.lortseam.completeconfig.api.ConfigCategory;
import me.lortseam.completeconfig.data.CollectionMap;
import me.lortseam.completeconfig.serialization.CollectionMapDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

public class Config extends CollectionMap {

    private static final Logger LOGGER = LogManager.getLogger();

    Config(String modID, List<ConfigCategory> topLevelCategories, JsonNode yaml) {
        super("config." + modID);
        for (ConfigCategory category : topLevelCategories) {
            if (!fill(modTranslationKey, category)) {
                continue;
            }
            try {
                new YAMLMapper().registerModule(new SimpleModule()
                        .addDeserializer(CollectionMapDeserializer.TYPE, new CollectionMapDeserializer(this, category.getConfigCategoryID()))
                ).readValue(yaml.traverse(), CollectionMapDeserializer.TYPE);
            } catch (IOException e) {
                LOGGER.warn("[CompleteConfig] An error occurred while trying to load the config for category " + category.getClass());
            }
        }
    }

    public String getModTranslationKey() {
        return modTranslationKey;
    }

}
