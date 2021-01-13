package me.lortseam.completeconfig.data;

import me.lortseam.completeconfig.ConfigHandler;
import me.lortseam.completeconfig.ModController;
import me.lortseam.completeconfig.gui.GuiBuilder;
import me.lortseam.completeconfig.io.ConfigSource;
import me.lortseam.completeconfig.api.ConfigGroup;
import me.lortseam.completeconfig.data.text.TranslationIdentifier;
import me.lortseam.completeconfig.util.TypeUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

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
    public static Builder builder(String modID) {
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

    public final static class Builder {

        private static final Logger LOGGER = LogManager.getLogger();

        private final String modID;
        private String[] branch = new String[0];
        private final List<ConfigGroup> topLevelGroups = new ArrayList<>();
        private TypeSerializerCollection typeSerializers;
        private GuiBuilder guiBuilder;

        private Builder(String modID) {
            this.modID = Objects.requireNonNull(modID);
        }

        /**
         * Sets the branch.
         *
         * <p>The branch determines the location of the config's save file.
         *
         * @param branch the branch
         * @return this builder
         */
        public Builder setBranch(String[] branch) {
            this.branch = Objects.requireNonNull(branch);
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
         * Registers custom type serializers, applied only to this config.
         *
         * <p>To register type serializers for every config of your mod, use
         * {@link ModController#registerTypeSerializers(TypeSerializerCollection)}.
         *
         * @param typeSerializers the type serializers
         * @return this builder
         */
        public Builder registerTypeSerializers(TypeSerializerCollection typeSerializers) {
            this.typeSerializers = TypeUtils.mergeSerializers(this.typeSerializers, Objects.requireNonNull(typeSerializers));
            return this;
        }

        /**
         * Sets a custom client GUI builder for the config.
         *
         * @param guiBuilder a GUI builder
         * @return this builder
         */
        @Environment(EnvType.CLIENT)
        public Builder setGuiBuilder(GuiBuilder guiBuilder) {
            this.guiBuilder = Objects.requireNonNull(guiBuilder);
            return this;
        }

        /**
         * Completes the config creation.
         *
         * @return the handler associated with the created config
         */
        public ConfigHandler build() {
            if (topLevelGroups.isEmpty()) {
                LOGGER.warn("[CompleteConfig] Mod " + modID + " tried to create an empty config!");
                return null;
            }
            return new ConfigHandler(new Config(new ConfigSource(ModController.of(modID), branch, typeSerializers), topLevelGroups), guiBuilder);
        }

    }

}
