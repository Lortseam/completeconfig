package me.lortseam.completeconfig.api;

import me.lortseam.completeconfig.ConfigBuilder;

public interface ConfigOwner {

    default void onInitializeConfig(ConfigBuilder builder) {}

    default void onInitializeClientConfig(ConfigBuilder builder) {
        onInitializeConfig(builder);
    }

    default void onInitializeServerConfig(ConfigBuilder builder) {
        onInitializeConfig(builder);
    }

    default String[] getConfigBranch() {
        return new String[0];
    }

}
