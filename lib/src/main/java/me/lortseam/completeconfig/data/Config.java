package me.lortseam.completeconfig.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.io.ConfigSource;
import me.lortseam.completeconfig.text.TranslationKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;

@Log4j2(topic = "CompleteConfig")
public abstract class Config extends BaseCollection implements ConfigContainer {

    /**
     * Creates a new config builder for the specified mod.
     *
     * @param modId the ID of the mod creating the config
     *
     * @deprecated Please subclass {@link Config}
     */
    @Deprecated
    public static Builder builder(String modId) {
        return new Builder(modId);
    }


    @Getter(AccessLevel.PACKAGE)
    private final ConfigSource source;
    @Environment(EnvType.CLIENT)
    private TranslationKey translation;

    /**
     * Creates a config and loads it.
     *
     * <p>The branch determines the location of the config's save file and has to be unique for the mod.
     *
     * @param modId the ID of the mod creating the config
     * @param branch the branch
     * @param saveOnExit whether to save the config when the client or server stops
     */
    protected Config(String modId, String[] branch, boolean saveOnExit) {
        source = new ConfigSource(modId, branch);
        ConfigRegistry.register(this);
        resolveContainer(this);
        if (isEmpty()) {
            logger.warn("Empty config: " + modId + " " + Arrays.toString(branch));
            return;
        }
        load();
        if (saveOnExit) {
            Runtime.getRuntime().addShutdownHook(new Thread(this::save));
        }
    }

    /**
     * Creates a config and loads it.
     *
     * @param modId the ID of the mod creating the config
     * @param saveOnExit whether to save the config when the client or server stops
     */
    protected Config(String modId, boolean saveOnExit) {
        this(modId, new String[0], saveOnExit);
    }

    public ModMetadata getMod() {
        return FabricLoader.getInstance().getModContainer(source.getModId()).get().getMetadata();
    }

    @Override
    public TranslationKey getTranslation() {
        return getTranslation(false);
    }

    @Environment(EnvType.CLIENT)
    public TranslationKey getTranslation(boolean includeBranch) {
        if (translation == null) {
            translation = TranslationKey.from(this);
        }
        if (includeBranch) {
            return translation.append(source.getBranch());
        }
        return translation;
    }

    private void load() {
        source.load(this);
    }

    /**
     * Saves the config.
     */
    public void save() {
        source.save(this);
    }

    @Log4j2(topic = "CompleteConfig")
    @Deprecated
    public final static class Builder {

        private final String modId;
        private String[] branch = new String[0];
        private final LinkedHashSet<ConfigContainer> children = new LinkedHashSet<>();
        private boolean main;
        private boolean saveOnExit;

        private Builder(String modId) {
            this.modId = modId;
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
        @Deprecated
        public Builder setBranch(String[] branch) {
            this.branch = branch;
            return this;
        }

        /**
         * Adds one or more containers to the config.
         *
         * @param containers one or more containers
         * @return this builder
         *
         * @deprecated Add the containers transitively to the config object
         */
        @Deprecated
        public Builder add(@NonNull ConfigContainer... containers) {
            for (ConfigContainer container : containers) {
                if (!children.add(Objects.requireNonNull(container))) {
                    throw new IllegalArgumentException("Duplicate container " + container.getClass().getSimpleName());
                }
            }
            return this;
        }

        /**
         * Sets a flag to save the config when the game closes.
         *
         * @return this builder
         */
        @Deprecated
        public Builder saveOnExit() {
            saveOnExit = true;
            return this;
        }

        /**
         * Registers the config as main mod config.
         *
         * @return this builder
         *
         * @deprecated Use {@link ConfigRegistry#setMainConfig(Config)}
         */
        @Deprecated
        public Builder main() {
            main = true;
            return this;
        }

        /**
         * Creates and loads the config.
         *
         * @return the created config, or null if empty
         */
        @Deprecated
        public Config build() {
            Config config = new Config(modId, branch, saveOnExit) {
                @Override
                public ConfigContainer[] getTransitives() {
                    return children.toArray(new ConfigContainer[0]);
                }
            };
            if (main) {
                ConfigRegistry.setMainConfig(config);
            }
            return config;
        }

    }


}
