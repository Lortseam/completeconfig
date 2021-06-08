package me.lortseam.completeconfig.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.io.ConfigSource;
import me.lortseam.completeconfig.text.TranslationKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;

import java.util.Arrays;
import java.util.Objects;

/**
 * The base config class. Instantiate or inherit this class to create a config for your mod.
 */
@Log4j2(topic = "CompleteConfig")
@ToString(onlyExplicitlyIncluded = true)
public class Config extends BaseCollection {

    @Getter(AccessLevel.PACKAGE)
    @ToString.Include
    private final ConfigSource source;
    private Runnable resolver;
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
    public Config(String modId, String[] branch, @NonNull ConfigContainer... containers) {
        source = new ConfigSource(modId, branch);
        ConfigRegistry.register(this);
        Arrays.stream(containers).forEach(Objects::requireNonNull);
        resolver = () -> {
            if (this instanceof ConfigContainer) {
                resolve((ConfigContainer) this);
            }
            resolve(containers);
            if (isEmpty()) {
                logger.warn(this + " is empty");
            }
        };
    }

    /**
     * Creates a config with the default branch.
     *
     * @param modId the ID of the mod creating the config
     */
    public Config(String modId, ConfigContainer... containers) {
        this(modId, new String[0], containers);
    }

    public final ModMetadata getMod() {
        return FabricLoader.getInstance().getModContainer(source.getModId()).get().getMetadata();
    }

    @Override
    public final TranslationKey getTranslation() {
        return getTranslation(false);
    }

    @Environment(EnvType.CLIENT)
    public final TranslationKey getTranslation(boolean includeBranch) {
        if (translation == null) {
            translation = TranslationKey.from(this);
        }
        if (includeBranch) {
            return translation.append(source.getBranch());
        }
        return translation;
    }

    /**
     * Loads the config.
     */
    public final void load() {
        if (resolver != null) {
            resolver.run();
            resolver = null;
        }
        if (isEmpty()) return;
        source.load(this);
    }

    /**
     * Saves the config.
     */
    public final void save() {
        if (resolver != null) {
            throw new IllegalStateException("Cannot save config before it was loaded");
        }
        if (isEmpty()) return;
        source.save(this);
    }

}
