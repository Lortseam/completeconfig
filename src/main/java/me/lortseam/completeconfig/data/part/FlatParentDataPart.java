package me.lortseam.completeconfig.data.part;

import org.spongepowered.configurate.CommentedConfigurationNode;

public interface FlatParentDataPart<CV extends DataPart> extends ParentDataPart<CV, CV> {

    @Override
    default CommentedConfigurationNode navigateToChild(CommentedConfigurationNode node, CV childValue) {
        return node;
    }

    @Override
    default CV retrieveChildValue(CV childValue) {
        return childValue;
    }

}
