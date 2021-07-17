package me.lortseam.completeconfig.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import me.lortseam.completeconfig.CompleteConfig;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.data.serialize.ClientSerializers;
import me.lortseam.completeconfig.extensions.CompleteConfigExtension;
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

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

/**
 * The base config class. Instantiate or inherit this class to create a config for your mod.
 */
@Log4j2(topic = "CompleteConfig")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public class Config extends BaseCollection {

    private static final TypeSerializerCollection GLOBAL_TYPE_SERIALIZERS;

    static {
        TypeSerializerCollection.Builder builder = TypeSerializerCollection.builder();
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            builder.registerAll(ClientSerializers.COLLECTION);
        }
        GLOBAL_TYPE_SERIALIZERS = builder.build();
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
     * <p>The branch determines the location of the config's save file and has to be mod-unique.
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
        Path path = FabricLoader.getInstance().getConfigDir();
        String[] subPath = ArrayUtils.add(branch, 0, modId);
        subPath[subPath.length - 1] = subPath[subPath.length - 1] + ".conf";
        for (String child : subPath) {
            path = path.resolve(child);
        }
        loader = HoconConfigurationLoader.builder()
                .path(path)
                .defaultOptions(options -> options.serializers(builder -> {
                    builder.registerAll(GLOBAL_TYPE_SERIALIZERS);
                    for (TypeSerializerCollection typeSerializers : CompleteConfig.collectExtensions(CompleteConfigExtension.class, CompleteConfigExtension::getTypeSerializers)) {
                        builder.registerAll(typeSerializers);
                    }
                }))
                .build();
        resolver = () -> {
            if (this instanceof ConfigContainer) {
                resolve((ConfigContainer) this);
            }
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
            translation = TranslationKey.from(this);
        }
        if (includeBranch) {
            return translation.append(branch);
        }
        return translation;
    }

    /**
     * Loads the config.
     *
     * <p>On first load, this also resolves the config's children.
     */
    public final void load() {
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
            logger.error("Failed to load config from file", e);
        }
        save();
    }

    /**
     * Saves the config.
     */
    public final void save() {
        if (resolver != null) {
            throw new IllegalStateException("Cannot save config before it was loaded");
        }
        if (isEmpty()) return;
        CommentedConfigurationNode root = loader.createNode();
        fetch(root);
        try {
            loader.save(root);
        } catch (ConfigurateException e) {
            logger.error("Failed to save config to file", e);
        }
    }

}
