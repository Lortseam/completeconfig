package me.lortseam.completeconfig.data;

import me.lortseam.completeconfig.api.ConfigGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CollectionMap extends ConfigMap<Collection> {

    private static final Logger LOGGER = LogManager.getLogger();

    protected CollectionMap(String modTranslationKey) {
        super(modTranslationKey);
    }

    protected boolean fill(String parentTranslationKey, ConfigGroup group) {
        String groupID = group.getConfigGroupID();
        Collection collection = new Collection(modTranslationKey, parentTranslationKey, group);
        if (collection.getEntries().isEmpty() && collection.getCollections().isEmpty()) {
            LOGGER.warn("[CompleteConfig] Group " + groupID + " is empty!");
            return false;
        }
        putUnique(groupID, collection);
        return true;
    }

}
