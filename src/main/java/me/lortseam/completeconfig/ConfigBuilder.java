package me.lortseam.completeconfig;

import me.lortseam.completeconfig.api.ConfigCategory;
import me.lortseam.completeconfig.api.ConfigOwner;
import me.lortseam.completeconfig.gui.GuiBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class ConfigBuilder {

    static ConfigBuilder create(String modID, String[] branch, Class<? extends ConfigOwner> owner) {
        return new ConfigBuilder(modID, branch, owner);
    }

    private final String modID;
    private final String[] branch;
    private final Class<? extends ConfigOwner> owner;
    private final List<ConfigCategory> topLevelCategories = new ArrayList<>();
    private GuiBuilder guiBuilder;

    private ConfigBuilder(String modID, String[] branch, Class<? extends ConfigOwner> owner) {
        this.modID = modID;
        this.branch = branch;
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
     * Sets a custom client GUI builder for the config.
     *
     * @param guiBuilder The GUI builder
     * @return this config builder
     */
    public ConfigBuilder setGuiBuilder(GuiBuilder guiBuilder) {
        Objects.requireNonNull(guiBuilder);
        this.guiBuilder = guiBuilder;
        return this;
    }

    /**
     * Completes the config creation.
     *
     * @return the handler associated with the created config
     */
    public ConfigHandler finish() {
        return new ConfigHandler(modID, branch, owner, topLevelCategories, guiBuilder);
    }

}
