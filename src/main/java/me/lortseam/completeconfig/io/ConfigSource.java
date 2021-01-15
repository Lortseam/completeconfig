package me.lortseam.completeconfig.io;

import lombok.Getter;
import me.lortseam.completeconfig.ModController;
import me.lortseam.completeconfig.data.Config;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class ConfigSource {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final TypeSerializerCollection GLOBAL_TYPE_SERIALIZERS = TypeSerializerCollection.builder()
            .registerExact(TextColorSerializer.INSTANCE)
            .build();
    private static final Set<ConfigSource> sources = new HashSet<>();

    @Getter
    private final String modID;
    private final String[] branch;
    private final HoconConfigurationLoader loader;

    public ConfigSource(ModController mod, String[] branch) {
        this.modID = mod.getID();
        this.branch = branch;
        if (!sources.add(this)) {
            throw new IllegalArgumentException("A config of the mod " + modID + " with the specified branch " + Arrays.toString(branch) + " already exists!");
        }
        String[] subPath = ArrayUtils.add(branch, 0, modID);
        subPath[subPath.length - 1] = subPath[subPath.length - 1] + ".conf";
        Path filePath = Paths.get(FabricLoader.getInstance().getConfigDir().toString(), subPath);
        loader = HoconConfigurationLoader.builder()
                .path(filePath)
                .defaultOptions(options -> options.serializers(builder -> {
                    builder.registerAll(GLOBAL_TYPE_SERIALIZERS);
                    builder.registerAll(mod.getTypeSerializers());
                }))
                .build();
    }

    public void load(Config config) {
        try {
            CommentedConfigurationNode root = loader.load();
            if (!root.virtual()) {
                config.apply(root);
            }
        } catch (ConfigurateException e) {
            LOGGER.error("[CompleteConfig] Failed to load config from file!", e);
        }
    }

    public void save(Config config) {
        CommentedConfigurationNode root = loader.createNode();
        config.fetch(root);
        try {
            loader.save(root);
        } catch (ConfigurateException e) {
            LOGGER.error("[CompleteConfig] Failed to save config to file!", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigSource that = (ConfigSource) o;
        return modID.equals(that.modID) && Arrays.equals(branch, that.branch);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(modID);
        result = 31 * result + Arrays.hashCode(branch);
        return result;
    }

}
