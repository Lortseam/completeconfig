package me.lortseam.completeconfig.data.structure;

import org.spongepowered.configurate.CommentedConfigurationNode;

public interface StructurePart {

    void apply(CommentedConfigurationNode node);

    void fetch(CommentedConfigurationNode node);

}
