package me.lortseam.completeconfig.data.part;

import org.spongepowered.configurate.CommentedConfigurationNode;

public interface DataPart {

    void apply(CommentedConfigurationNode node);

    void fetch(CommentedConfigurationNode node);

}
