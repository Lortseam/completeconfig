package me.lortseam.completeconfig.io;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import me.lortseam.completeconfig.CompleteConfig;
import me.lortseam.completeconfig.data.Config;
import me.lortseam.completeconfig.extensions.CompleteConfigExtension;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

@Log4j2(topic = "CompleteConfig")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public final class ConfigSource {

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

    public ConfigSource(@NonNull String modId, @NonNull String[] branch) {
        if (!FabricLoader.getInstance().isModLoaded(modId)) {
            throw new IllegalArgumentException("Mod " + modId + " is not loaded");
        }
        Arrays.stream(branch).forEach(Objects::requireNonNull);
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
    }

    public void load(Config config) {
        try {
            CommentedConfigurationNode root = loader.load();
            if (!root.isNull()) {
                config.apply(root);
            }
        } catch (ConfigurateException e) {
            logger.error("Failed to load config from file", e);
        }
        save(config);
    }

    public void save(Config config) {
        CommentedConfigurationNode root = loader.createNode();
        config.fetch(root);
        try {
            loader.save(root);
        } catch (ConfigurateException e) {
            logger.error("Failed to save config to file", e);
        }
    }

}
