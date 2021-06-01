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

import java.util.LinkedHashSet;
import java.util.Objects;

/**
 * The base config class. Instantiate or inherit this class to create a config for your mod.
 */
@Log4j2(topic = "CompleteConfig")
public class Config extends BaseCollection {

    /**
     * Creates a new config builder for the specified mod.
     *
     * @param modId the ID of the mod creating the config
     *
     * @deprecated Use {@link Config} class directly
     */
    @Deprecated
    public static Builder builder(String modId) {
        return new Builder(modId);
    }

    @Getter(AccessLevel.PACKAGE)
    private final ConfigSource source;
    private boolean loaded = false;
    @Environment(EnvType.CLIENT)
    private TranslationKey translation;

    /**
     * Creates a config with the specified branch.
     *
     * <p>The branch determines the location of the config's save file and has to be mod-unique.
     *
     * @param modId the ID of the mod creating the config
     * @param branch the branch
     */
    public Config(String modId, String[] branch) {
        source = new ConfigSource(modId, branch);
        ConfigRegistry.register(this);
    }

    /**
     * Creates a config with the default branch.
     *
     * @param modId the ID of the mod creating the config
     */
    public Config(String modId) {
        this(modId, new String[0]);
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

    /**
     * Adds one or more containers to the config.
     *
     * @param containers one or more containers
     * @return this config
     */
    public Config add(ConfigContainer... containers) {
        if (loaded) {
            throw new IllegalStateException("Cannot add container(s) after config was loaded already");
        }
        resolve(containers);
        return this;
    }

    /**
     * Loads the config.
     *
     * @return this config
     */
    public Config load() {
        if(!isEmpty()) {
            source.load(this);
        }
        loaded = true;
        return this;
    }

    /**
     * Saves the config.
     */
    public void save() {
        if(isEmpty()) return;
        source.save(this);
    }

    @Log4j2(topic = "CompleteConfig")
    @Deprecated
    public final static class Builder {

        private final String modId;
        private String[] branch = new String[0];
        private final LinkedHashSet<ConfigContainer> children = new LinkedHashSet<>();
        private boolean main;

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
         * @deprecated Use {@link Config#add(ConfigContainer...)}
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
         *
         * @deprecated Use {@link Config#load()}
         */
        @Deprecated
        public Config build() {
            Config config = new Config(modId, branch).add(children.toArray(new ConfigContainer[0])).load();
            if (main) {
                ConfigRegistry.setMainConfig(config);
            }
            return config;
        }

    }


}
