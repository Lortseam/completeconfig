package me.lortseam.completeconfig.collection;

import me.lortseam.completeconfig.ConfigMap;
import me.lortseam.completeconfig.api.ConfigCategory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CollectionMap extends ConfigMap<Collection> {

    private static final Logger LOGGER = LogManager.getLogger();

    protected CollectionMap(String modTranslationKey) {
        super(modTranslationKey);
    }

    protected boolean fill(String parentTranslationKey, ConfigCategory category) {
        String categoryID = category.getConfigCategoryID();
        Collection collection = new Collection(modTranslationKey, parentTranslationKey, category);
        if (collection.getEntries().isEmpty() && collection.getCollections().isEmpty()) {
            LOGGER.warn("[CompleteConfig] Category " + categoryID + " is empty!");
            return false;
        }
        put(categoryID, collection);
        return true;
    }

}
