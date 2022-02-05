package me.lortseam.completeconfig.data;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.AbstractConfigurationLoader;

public enum ConfigFormat {

    HOCON() {

        @Override
        public AbstractConfigurationLoader.Builder<?, ? extends AbstractConfigurationLoader<CommentedConfigurationNode>> createLoaderBuilder() {
            return HoconConfigurationLoader.builder();
        }

    };

    public abstract AbstractConfigurationLoader.Builder<?, ? extends AbstractConfigurationLoader<CommentedConfigurationNode>> createLoaderBuilder();

}
