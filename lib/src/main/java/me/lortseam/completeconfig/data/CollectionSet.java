package me.lortseam.completeconfig.data;

import lombok.extern.log4j.Log4j2;
import me.lortseam.completeconfig.api.ConfigGroup;
import me.lortseam.completeconfig.data.text.TranslationIdentifier;

@Log4j2(topic = "CompleteConfig")
public class CollectionSet extends DataSet<Collection> {

    protected CollectionSet(TranslationIdentifier translation) {
        super(translation);
    }

    void resolve(ConfigGroup group) {
        String groupID = group.getID();
        Collection collection = new Collection(groupID, translation.append(groupID), group.getTooltipTranslationKeys(), group.getComment());
        collection.resolveContainer(group);
        if (collection.isEmpty()) {
            logger.warn("Group " + groupID + " is empty");
            return;
        }
        add(collection);
    }

}
