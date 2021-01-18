package me.lortseam.completeconfig.data;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import me.lortseam.completeconfig.ConfigHandler;
import me.lortseam.completeconfig.api.ConfigGroup;
import me.lortseam.completeconfig.data.text.TranslationIdentifier;
import me.lortseam.completeconfig.gui.GuiBuilder;
import me.lortseam.completeconfig.io.ConfigSource;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Config extends CollectionMap {

    /**
     * Creates a new config builder for the specified mod.
     *
     * @param modID the ID of the mod creating the config
     */
    public static Builder builder(@NonNull String modID) {
        if (!FabricLoader.getInstance().isModLoaded(modID)) {
            throw new IllegalArgumentException("Mod " + modID + " is not loaded");
        }
        return new Builder(modID);
    }

    private final ConfigSource source;

    private Config(ConfigSource source, List<ConfigGroup> topLevelGroups) {
        super(new TranslationIdentifier(source.getModID()));
        this.source = source;
        for (ConfigGroup group : topLevelGroups) {
            resolve(group);
        }
    }

    public String getModID() {
        return source.getModID();
    }

    public TranslationIdentifier getTranslation() {
        return translation;
    }

    public void load() {
        source.load(this);
    }

    public void save() {
        source.save(this);
    }

    @Log4j2
    public final static class Builder {

        private final String modID;
        private String[] branch = new String[0];
        private final List<ConfigGroup> topLevelGroups = new ArrayList<>();
        private GuiBuilder guiBuilder;

        private Builder(String modID) {
            this.modID = modID;
        }

        /**
         * Sets the branch. Every config of a mod needs a unique branch, therefore setting a branch is only required
         * when using more than one config.
         *
         * <p>The branch determines the location of the config's save file.
         *
         * @param branch the branch
         * @return this builder
         */
        public Builder setBranch(@NonNull String[] branch) {
            this.branch = branch;
            return this;
        }

        /**
         * Adds one or more top-level groups to the config.
         *
         * @param groups one or more top-level groups
         * @return this builder
         */
        public Builder add(ConfigGroup... groups) {
            Arrays.stream(groups).forEach(Objects::requireNonNull);
            topLevelGroups.addAll(Arrays.asList(groups));
            return this;
        }

        /**
         * Sets a custom client GUI builder.
         *
         * @param guiBuilder a GUI builder
         * @return this builder
         */
        @Environment(EnvType.CLIENT)
        public Builder setGuiBuilder(@NonNull GuiBuilder guiBuilder) {
            this.guiBuilder = guiBuilder;
            return this;
        }

        /**
         * Completes the config creation.
         *
         * @return the handler associated with the created config
         */
        public ConfigHandler build() {
            if (topLevelGroups.isEmpty()) {
                logger.warn("[CompleteConfig] Mod " + modID + " tried to create an empty config!");
                return null;
            }
            return new ConfigHandler(new Config(new ConfigSource(modID, branch), topLevelGroups), guiBuilder);
        }

    }

}
