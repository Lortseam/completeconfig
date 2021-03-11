package me.lortseam.completeconfig.data.structure;

import org.spongepowered.configurate.CommentedConfigurationNode;

public interface DataPart {

    default String getID() {
        throw new UnsupportedOperationException();
    }

    void apply(CommentedConfigurationNode node);

    void fetch(CommentedConfigurationNode node);

}
