package me.lortseam.completeconfig.api;

import me.lortseam.completeconfig.ConfigBuilder;

/**
 * This entrypoint is called to create a config in a specific environment or in both environments.
 *
 * <p>In {@code fabric.mod.json}, the entrypoint is defined with {@code completeconfig} key.</p>
 */
public interface ConfigOwner {

    /**
     * Called on the client side and server side to initialize a config associated with this entrypoint's mod.
     *
     * @param builder The config builder
     */
    default void onInitializeConfig(ConfigBuilder builder) {}

    /**
     * Called on the client side to initialize a config associated with this entrypoint's mod.
     *
     * @param builder The config builder
     */
    default void onInitializeClientConfig(ConfigBuilder builder) {
        onInitializeConfig(builder);
    }

    /**
     * Called on the server side to initialize a config associated with this entrypoint's mod.
     *
     * @param builder The config builder
     */
    default void onInitializeServerConfig(ConfigBuilder builder) {
        onInitializeConfig(builder);
    }

    /**
     * Used to set a specific branch for the configuration(s) created by this entrypoint.
     *
     * <p>A config branch defines the location of the config's save file. The root of this branch is always the mod ID.
     *
     * @return the config branch
     */
    default String[] getConfigBranch() {
        return new String[0];
    }

}
