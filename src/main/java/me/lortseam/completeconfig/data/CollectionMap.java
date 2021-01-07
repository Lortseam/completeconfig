package me.lortseam.completeconfig.data;

import me.lortseam.completeconfig.api.ConfigGroup;
import me.lortseam.completeconfig.data.gui.TranslationIdentifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;

public class CollectionMap extends ConfigMap<Collection> implements DataPart<ConfigGroup> {

    private static final Logger LOGGER = LogManager.getLogger();

    protected CollectionMap(TranslationIdentifier translation) {
        super(translation);
    }

    @Override
    public void resolve(ConfigGroup group) {
        String groupID = group.getConfigGroupID();
        Collection collection = new Collection(translation.append(groupID));
        collection.resolve(group);
        if (collection.getEntries().isEmpty() && collection.getCollections().isEmpty()) {
            LOGGER.warn("[CompleteConfig] Group " + groupID + " is empty!");
            return;
        }
        putUnique(groupID, collection);
    }

    @Override
    public void apply(CommentedConfigurationNode node) {
        forEach((id, collection) -> {
            CommentedConfigurationNode collectionNode = node.node(id);
            if(collectionNode.virtual()) return;
            collection.apply(collectionNode);
        });
    }

    @Override
    public void fetch(CommentedConfigurationNode node) {
        forEach((id, collection) -> collection.fetch(node.node(id)));
    }

}
