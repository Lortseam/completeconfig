package me.lortseam.completeconfig;

import me.lortseam.completeconfig.api.ConfigGroup;
import me.lortseam.completeconfig.api.ConfigOwner;
import me.lortseam.completeconfig.gui.GuiBuilder;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class ConfigBuilder {

    private final String modID;
    private final String[] branch;
    private final Class<? extends ConfigOwner> owner;
    private final List<ConfigGroup> topLevelGroups = new ArrayList<>();
    private TypeSerializerCollection typeSerializers;
    private GuiBuilder guiBuilder;

    ConfigBuilder(String modID, String[] branch, Class<? extends ConfigOwner> owner) {
        this.modID = modID;
        this.branch = branch;
        this.owner = owner;
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

    //TODO: Add javadoc
    public ConfigBuilder registerTypeSerializers(TypeSerializerCollection typeSerializers) {
        Objects.requireNonNull(typeSerializers);
        if (this.typeSerializers == null) {
            this.typeSerializers = typeSerializers;
        } else {
            this.typeSerializers = TypeSerializerCollection.builder()
                    .registerAll(this.typeSerializers)
                    .registerAll(typeSerializers)
                    .build();
        }
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
        return ConfigHandler.buildConfig(modID, branch, owner, topLevelGroups, typeSerializers, guiBuilder);
    }

    /**
     * @deprecated Use {@link #build()}
     */
    @Deprecated
    public ConfigHandler finish() {
        return build();
    }

}
