package me.lortseam.completeconfig.data;

import me.lortseam.completeconfig.api.ConfigGroup;
import me.lortseam.completeconfig.data.text.TranslationIdentifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CollectionMap extends ConfigMap<Collection> {

    private static final Logger LOGGER = LogManager.getLogger();

    protected CollectionMap(TranslationIdentifier translation) {
        super(translation);
    }

    void resolve(ConfigGroup group) {
        String groupID = group.getConfigGroupID();
        Collection collection = new Collection(translation.append(groupID));
        collection.resolve(group);
        if (collection.getEntries().isEmpty() && collection.getCollections().isEmpty()) {
            LOGGER.warn("[CompleteConfig] Group " + groupID + " is empty!");
            return;
        }
        putUnique(groupID, collection);
    }

}
