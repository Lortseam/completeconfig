package me.lortseam.completeconfig;

import me.lortseam.completeconfig.api.ConfigCategory;
import me.lortseam.completeconfig.api.ConfigOwner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ConfigBuilder {

    static ConfigBuilder create(String modID, String[] branch, Class<? extends ConfigOwner> owner) {
        return new ConfigBuilder(modID, branch, owner);
    }

    private final ConfigHandler handler;
    private final Class<? extends ConfigOwner> owner;
    private final List<ConfigCategory> topLevelCategories = new ArrayList<>();

    private ConfigBuilder(String modID, String[] branch, Class<? extends ConfigOwner> owner) {
        handler = new ConfigHandler(modID, branch);
        this.owner = owner;
    }

    /**
     * Adds one or multiple top-level categories to the config.
     *
     * @param categories the top-level categories
     * @return this config builder
     */
    public ConfigBuilder add(ConfigCategory... categories) {
        topLevelCategories.addAll(Arrays.asList(categories));
        return this;
    }

    /**
     * Completes the config creation and registers the config.
     *
     * @return the handler associated with the created config
     */
    public ConfigHandler finish() {
        handler.register(owner, topLevelCategories);
        return handler;
    }

}
