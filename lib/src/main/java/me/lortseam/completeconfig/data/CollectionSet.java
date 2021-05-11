package me.lortseam.completeconfig.data;

import lombok.extern.log4j.Log4j2;
import me.lortseam.completeconfig.api.ConfigGroup;
import me.lortseam.completeconfig.data.text.TranslationKey;

@Log4j2(topic = "CompleteConfig")
public class CollectionSet extends DataSet<Collection> {

    protected CollectionSet(TranslationKey translation) {
        super(translation);
    }

    void resolve(ConfigGroup group) {
        String groupId = group.getId();
        Collection collection = new Collection(groupId, translation.append(groupId), group.getTooltipTranslationKeys(), group.getComment());
        collection.resolveContainer(group);
        if (collection.isEmpty()) {
            logger.warn("Empty group: " + groupId);
            return;
        }
        add(collection);
    }

}
