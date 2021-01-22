package me.lortseam.completeconfig.data.structure;

import org.spongepowered.configurate.CommentedConfigurationNode;

public interface FlatDataPart<CV extends DataPart> extends ParentDataPart<CV, CV> {

    @Override
    default CommentedConfigurationNode navigateToChild(CommentedConfigurationNode node, CV childValue) {
        return node;
    }

    @Override
    default CV retrieveChildValue(CV childValue) {
        return childValue;
    }

}
