package me.lortseam.completeconfig;

import me.lortseam.completeconfig.api.ConfigGroup;
import me.lortseam.completeconfig.gui.GuiBuilder;
import me.lortseam.completeconfig.util.TypeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class ConfigBuilder {

    private static final Logger LOGGER = LogManager.getLogger();

    private final String modID;
    private final String[] branch;
    private final List<ConfigGroup> topLevelGroups = new ArrayList<>();
    private TypeSerializerCollection typeSerializers;
    private GuiBuilder guiBuilder;

    /**
     * Creates a new config builder for the specified mod with a custom branch.
     *
     * <p>The config branch determines the location of the config's save file.
     *
     * @param modID the ID of the mod creating the config
     */
    public ConfigBuilder(String modID, String[] branch) {
        this.modID = modID;
        this.branch = branch;
    }

    /**
     * Creates a new config builder for the specified mod.
     *
     * @param modID the ID of the mod creating the config
     */
    public ConfigBuilder(String modID) {
        this(modID, new String[0]);
    }

    /**
     * Adds one or more top-level groups to the config.
     *
     * @param groups one or more top-level groups
     * @return this config builder
     */
    public ConfigBuilder add(ConfigGroup... groups) {
        topLevelGroups.addAll(Arrays.asList(groups));
        return this;
    }

    /**
     * Registers custom type serializers for config saving and loading.
     *
     * @param typeSerializers the type serializers
     * @return this config builder
     */
    public ConfigBuilder registerTypeSerializers(TypeSerializerCollection typeSerializers) {
        this.typeSerializers = TypeUtils.mergeSerializers(this.typeSerializers, Objects.requireNonNull(typeSerializers));
        return this;
    }

    /**
     * Sets a custom client GUI builder for the config.
     *
     * @param guiBuilder a GUI builder
     * @return this config builder
     */
    public ConfigBuilder setGuiBuilder(GuiBuilder guiBuilder) {
        this.guiBuilder = Objects.requireNonNull(guiBuilder);
        return this;
    }

    /**
     * Completes the config creation and registers the config.
     *
     * @return the handler associated with the created config
     */
    public ConfigHandler build() {
        if (topLevelGroups.isEmpty()) {
            LOGGER.warn("[CompleteConfig] Mod " + modID + " tried to create an empty config!");
            return null;
        }
        return new ConfigHandler(new ConfigSource(modID, branch, typeSerializers), topLevelGroups, guiBuilder);
    }

}
