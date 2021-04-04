package me.lortseam.completeconfig.data;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.data.text.TranslationIdentifier;
import me.lortseam.completeconfig.io.ConfigSource;
import net.fabricmc.loader.api.FabricLoader;

import java.util.*;

@Log4j2
public class Config extends Node {

    private static final Set<Config> configs = new HashSet<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (Config config : configs) {
                if(!config.saveOnExit) continue;
                config.save();
            }
        }));
    }

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
    private final boolean saveOnExit;

    private Config(ConfigSource source, LinkedHashSet<ConfigContainer> children, boolean saveOnExit) {
        super(TranslationIdentifier.ofRoot(source.getModID()));
        this.source = source;
        this.saveOnExit = saveOnExit;
        resolve(children);
        if (isEmpty()) {
            logger.warn("[CompleteConfig] Config of " + source + " is empty!");
            return;
        }
        source.load(this);
        configs.add(this);
    }

    public String getModID() {
        return source.getModID();
    }

    public TranslationIdentifier getTranslation() {
        return translation;
    }

    public void save() {
        source.save(this);
    }

    @Log4j2
    public final static class Builder {

        private final String modID;
        private String[] branch = new String[0];
        private final LinkedHashSet<ConfigContainer> children = new LinkedHashSet<>();
        private boolean saveOnExit = true;

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
            Arrays.stream(containers).forEach(Objects::requireNonNull);
            for (ConfigContainer container : containers) {
                if (!children.add(container)) {
                    throw new IllegalArgumentException("Duplicate container");
                }
            }
            return this;
        }

        public Builder setSaveOnExit(boolean saveOnExit) {
            this.saveOnExit = saveOnExit;
            return this;
        }

        /**
         * Completes the config creation.
         *
         * @return the created config
         */
        public Config build() {
            if (children.isEmpty()) {
                logger.warn("[CompleteConfig] Mod " + modID + " tried to create an empty config!");
                return null;
            }
            return new Config(new ConfigSource(modID, branch), children, saveOnExit);
        }

    }

}
