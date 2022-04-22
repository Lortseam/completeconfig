package me.lortseam.completeconfig.data;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.text.TranslationKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * The base config class. Instantiate or inherit this class to create a config for your mod.
 */
@Slf4j(topic = "CompleteConfig")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public class Config extends Parent implements ConfigContainer {

    private final ConfigOptions options;
    private final HoconConfigurationLoader loader;
    private Runnable resolver;
    @Environment(EnvType.CLIENT)
    private TranslationKey translation;

    public Config(@NonNull ConfigOptions.Builder optionsBuilder, @NonNull ConfigContainer... containers) {
        Arrays.stream(containers).forEach(Objects::requireNonNull);
        this.options = optionsBuilder.build();
        loader = options.createDefaultLoader();
        resolver = () -> {
            resolve(this);
            resolve(containers);
            if (isEmpty()) {
                logger.warn(this + " is empty");
            }
        };
        ConfigRegistry.register(this);
    }

    /**
     * Gets the metadata of the mod that owns this config.
     *
     * @return the mod that owns this config
     */
    public final ModMetadata getMod() {
        return FabricLoader.getInstance().getModContainer(options.getModId()).get().getMetadata();
    }

    @Override
    public final TranslationKey getNameTranslation() {
        return getTranslation(false);
    }

    @Environment(EnvType.CLIENT)
    public final TranslationKey getTranslation(boolean includeBranch) {
        if (translation == null) {
            translation = new TranslationKey(this);
        }
        if (includeBranch) {
            return translation.append(options.getBranch());
        }
        return translation;
    }

    private void deserialize(HoconConfigurationLoader loader) {
        if (resolver != null) {
            resolver.run();
            resolver = null;
        }
        if (isEmpty()) return;
        try {
            CommentedConfigurationNode root = loader.load();
            if (!root.isNull()) {
                apply(root);
            }
        } catch (ConfigurateException e) {
            logger.error("Failed to load config", e);
        }
    }

    /**
     * Deserializes values from a custom source and applies them to this config.
     *
     * @param source the source to deserialize from
     */
    public final void deserialize(Callable<BufferedReader> source) {
        deserialize(options.createLoader(builder -> builder.source(source)));
    }

    /**
     * Loads the config from the config file.
     */
    public final void load() {
        deserialize(loader);
        save();
    }

    private void serialize(HoconConfigurationLoader loader) {
        if (resolver != null) {
            throw new IllegalStateException("Cannot serialize config before it was loaded");
        }
        if (isEmpty()) return;
        CommentedConfigurationNode root = loader.createNode();
        fetch(root);
        try {
            loader.save(root);
        } catch (ConfigurateException e) {
            logger.error("Failed to serialize config", e);
        }
    }

    /**
     * Serializes this config's values to a custom sink.
     *
     * @param sink the sink to serialize to
     */
    public final void serialize(Callable<BufferedWriter> sink) {
        serialize(options.createLoader(builder -> builder.sink(sink)));
    }

    /**
     * Saves the config to the config file.
     */
    public final void save() {
        serialize(loader);
    }

    /**
     * Called when an entry of this config gets updated.
     */
    protected void onChildUpdate() {}

    @Override
    Config getRoot() {
        return this;
    }

}
