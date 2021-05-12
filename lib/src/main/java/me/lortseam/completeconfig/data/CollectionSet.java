package me.lortseam.completeconfig.data;

import lombok.extern.log4j.Log4j2;
import me.lortseam.completeconfig.api.ConfigGroup;

@Log4j2(topic = "CompleteConfig")
public class CollectionSet extends DataSet<Collection> {

    protected CollectionSet(BaseCollection parent) {
        super(parent);
    }

    void resolve(ConfigGroup group) {
        Collection collection = new Collection(parent, group);
        collection.resolveContainer(group);
        if (collection.isEmpty()) {
            logger.warn("Empty group: " + collection.getId());
            return;
        }
        add(collection);
    }

}
