package me.lortseam.completeconfig.data;

import org.spongepowered.configurate.CommentedConfigurationNode;

interface DataPart<T> {

    void resolve(T t);

    void apply(CommentedConfigurationNode node);

    void fetch(CommentedConfigurationNode node);

}
