package me.lortseam.completeconfig.api;

import me.lortseam.completeconfig.ConfigBuilder;

public interface ConfigOwner {

    default void onInitializeConfig(ConfigBuilder creator) {}

    default void onInitializeClientConfig(ConfigBuilder creator) {
        onInitializeConfig(creator);
    }

    default void onInitializeServerConfig(ConfigBuilder creator) {
        onInitializeConfig(creator);
    }

    default String[] getConfigBranch() {
        return new String[0];
    }

}
