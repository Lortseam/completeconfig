package me.lortseam.completeconfig.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import me.lortseam.completeconfig.CompleteConfig;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.data.extension.BaseExtension;
import me.lortseam.completeconfig.text.TranslationKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * The base config class. Instantiate or inherit this class to create a config for your mod.
 */
@Slf4j(topic = "CompleteConfig")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public class Config extends Parent implements ConfigContainer {

    private static HoconConfigurationLoader createLoader(Consumer<HoconConfigurationLoader.Builder> builderConsumer) {
        HoconConfigurationLoader.Builder builder = HoconConfigurationLoader.builder()
                .defaultOptions(options -> options.serializers(typeSerializerCollection -> {
                    for (TypeSerializerCollection typeSerializers : CompleteConfig.collectExtensions(BaseExtension.class, BaseExtension::getTypeSerializers)) {
                        typeSerializerCollection.registerAll(typeSerializers);
                    }
                }));
        builderConsumer.accept(builder);
        return builder.build();
    }

    @EqualsAndHashCode.Include
    @ToString.Include
    @Getter
    private final String modId;
    @EqualsAndHashCode.Include
    @ToString.Include
    @Getter
    private final String[] branch;
    private final HoconConfigurationLoader loader;
    private Runnable resolver;
    @Environment(EnvType.CLIENT)
    private TranslationKey translation;

    /**
     * Creates a config with the specified branch.
     *
     * <p>The branch determines the location of the config file and has to be mod-unique.
     *
     * @param modId the ID of the mod creating the config
     * @param branch the branch
     */
    public Config(@NonNull String modId, @NonNull String[] branch, @NonNull ConfigContainer... containers) {
        if (!FabricLoader.getInstance().isModLoaded(modId)) {
            throw new IllegalArgumentException("Mod " + modId + " is not loaded");
        }
        Arrays.stream(branch).forEach(Objects::requireNonNull);
        Arrays.stream(containers).forEach(Objects::requireNonNull);
        this.modId = modId;
        this.branch = branch;
        loader = createLoader(builder -> {
            Path path = FabricLoader.getInstance().getConfigDir();
            String[] subPath = ArrayUtils.addFirst(branch, modId);
            subPath[subPath.length - 1] = subPath[subPath.length - 1] + ".conf";
            for (String child : subPath) {
                path = path.resolve(child);
            }
            builder.path(path);
        });
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
     * Creates a config with the default branch.
     *
     * @param modId the ID of the mod creating the config
     */
    public Config(String modId, ConfigContainer... containers) {
        this(modId, new String[0], containers);
    }

    public final ModMetadata getMod() {
        return FabricLoader.getInstance().getModContainer(modId).get().getMetadata();
    }

    @Override
    public final TranslationKey getTranslation() {
        return getTranslation(false);
    }

    @Environment(EnvType.CLIENT)
    public final TranslationKey getTranslation(boolean includeBranch) {
        if (translation == null) {
            translation = new TranslationKey(this);
        }
        if (includeBranch) {
            return translation.append(branch);
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
        deserialize(createLoader(builder -> builder.source(source)));
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
        serialize(createLoader(builder -> builder.sink(sink)));
    }

    /**
     * Saves the config to the config file.
     */
    public final void save() {
        serialize(loader);
    }

}
