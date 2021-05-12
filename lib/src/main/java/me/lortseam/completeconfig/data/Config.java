package me.lortseam.completeconfig.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.data.text.TranslationKey;
import me.lortseam.completeconfig.io.ConfigSource;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;

@Log4j2(topic = "CompleteConfig")
public final class Config extends BaseCollection {

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (Config config : Registry.getConfigs()) {
                if (config.saveOnExit) {
                    config.save();
                }
            }
        }));
    }

    /**
     * Creates a new config builder for the specified mod.
     *
     * @param modId the ID of the mod creating the config
     */
    public static Builder builder(@NonNull String modId) {
        if (!FabricLoader.getInstance().isModLoaded(modId)) {
            throw new IllegalArgumentException("Mod " + modId + " is not loaded");
        }
        return new Builder(modId);
    }

    @Getter(AccessLevel.PACKAGE)
    private final ConfigSource source;
    @Environment(EnvType.CLIENT)
    private TranslationKey branchedTranslation;
    private final boolean saveOnExit;

    private Config(ConfigSource source, ConfigContainer[] children, boolean saveOnExit) {
        this.source = source;
        this.saveOnExit = saveOnExit;
        resolve(children);
    }

    public ModMetadata getMod() {
        return FabricLoader.getInstance().getModContainer(source.getModId()).get().getMetadata();
    }

    @Override
    public TranslationKey getTranslation() {
        if (translation == null) {
            translation = TranslationKey.from(source);
        }
        return translation;
    }

    @Environment(EnvType.CLIENT)
    public TranslationKey getBranchedTranslation() {
        if (branchedTranslation == null) {
            branchedTranslation = getTranslation().append(source.getBranch());
        }
        return branchedTranslation;
    }

    private void load() {
        source.load(this);
    }

    public void save() {
        source.save(this);
    }

    @Log4j2(topic = "CompleteConfig")
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
        public Builder setBranch(@NonNull String[] branch) {
            Arrays.stream(branch).forEach(Objects::requireNonNull);
            this.branch = branch;
            return this;
        }

        /**
         * Adds one or more containers to the config.
         *
         * @param containers one or more containers
         * @return this builder
         */
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
        public Builder saveOnExit() {
            saveOnExit = true;
            return this;
        }

        /**
         * Registers the config as main mod config.
         *
         * @return this builder
         */
        public Builder main() {
            main = true;
            return this;
        }

        /**
         * Creates and loads the config.
         *
         * @return the created config, or null if empty
         */
        public Config build() {
            Config config = null;
            if (!children.isEmpty()) {
                config = new Config(new ConfigSource(modId, branch), children.toArray(new ConfigContainer[0]), saveOnExit);
            }
            if (config == null || config.isEmpty()) {
                logger.warn("Empty config: " + modId + " " + Arrays.toString(branch));
                return null;
            }
            Registry.register(config, main || branch.length == 0 && !Registry.getMainConfig(modId).isPresent());
            config.load();
            return config;
        }

    }

}
