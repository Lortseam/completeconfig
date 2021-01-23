package me.lortseam.completeconfig.data;

import lombok.extern.log4j.Log4j2;
import me.lortseam.completeconfig.api.ConfigGroup;
import me.lortseam.completeconfig.data.text.TranslationIdentifier;

@Log4j2
public class CollectionMap extends ConfigMap<Collection> {

    protected CollectionMap(TranslationIdentifier translation) {
        super(translation);
    }

    void resolve(ConfigGroup group) {
        String groupID = group.getGroupID();
        Collection collection = new Collection(translation.append(groupID));
        collection.resolve(group);
        if (collection.getEntries().isEmpty() && collection.getCollections().isEmpty()) {
            logger.warn("[CompleteConfig] Group " + groupID + " is empty!");
            return;
        }
        putUnique(groupID, collection);
    }

}
