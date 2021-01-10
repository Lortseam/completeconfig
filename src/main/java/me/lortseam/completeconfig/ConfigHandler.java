package me.lortseam.completeconfig;

import me.lortseam.completeconfig.api.ConfigGroup;
import me.lortseam.completeconfig.api.ConfigOwner;
import me.lortseam.completeconfig.data.Config;
import me.lortseam.completeconfig.gui.GuiBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public final class ConfigHandler {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<Class<? extends ConfigOwner>, ConfigHandler> HANDLERS = new HashMap<>();
    private static final Map<String, List<String[]>> MOD_BRANCHES = new HashMap<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (ConfigHandler handler : HANDLERS.values()) {
                handler.save();
            }
        }));
    }

    static ConfigHandler registerConfig(String modID, String[] branch, Class<? extends ConfigOwner> owner, List<ConfigGroup> topLevelGroups, GuiBuilder guiBuilder) {
        if (HANDLERS.containsKey(owner)) {
            throw new IllegalArgumentException("The specified owner " + owner + " already created a config!");
        }
        if (topLevelGroups.isEmpty()) {
            LOGGER.warn("[CompleteConfig] Owner " + owner + " of mod " + modID + " tried to create an empty config!");
            return null;
        }
        List<String[]> branches = MOD_BRANCHES.computeIfAbsent(modID, key -> new ArrayList<>());
        if (branches.stream().anyMatch(presentBranch -> Arrays.equals(branch, presentBranch))) {
            throw new IllegalArgumentException("A config of the mod " + modID + " with the specified branch " + Arrays.toString(branch) + " already exists!");
        }
        branches.add(branch);
        String[] subPath = ArrayUtils.add(branch, 0, modID);
        subPath[subPath.length - 1] = subPath[subPath.length - 1] + ".conf";
        Path filePath = Paths.get(FabricLoader.getInstance().getConfigDir().toString(), subPath);
        ConfigHandler handler = new ConfigHandler(modID, filePath, topLevelGroups, guiBuilder);
        HANDLERS.put(owner, handler);
        return handler;
    }

    /**
     * Gets the {@link ConfigHandler} for the specified owner if that owner created a config before.
     *
     * @param owner The owner class of the config
     * @return The handler if one was found or else an empty result
     */
    public static Optional<ConfigHandler> of(Class<? extends ConfigOwner> owner) {
        return Optional.ofNullable(HANDLERS.get(owner));
    }

    private final HoconConfigurationLoader loader;
    private final Config config;
    private GuiBuilder guiBuilder;

    private ConfigHandler(String modID, Path filePath, List<ConfigGroup> topLevelGroups, GuiBuilder guiBuilder) {
        loader = HoconConfigurationLoader.builder()
                .path(filePath)
                .build();
        config = new Config(modID, topLevelGroups);
        CommentedConfigurationNode root = load();
        if (!root.virtual()) {
            config.apply(root);
        }
        this.guiBuilder = guiBuilder;
    }

    private CommentedConfigurationNode load() {
        try {
            return loader.load();
        } catch (ConfigurateException e) {
            LOGGER.error("[CompleteConfig] Failed to load config from file!", e);
        }
        return CommentedConfigurationNode.root();
    }

    /**
     * Generates the configuration GUI.
     *
     * @param parentScreen The parent screen
     * @return The generated configuration screen
     */
    @Environment(EnvType.CLIENT)
    public Screen buildScreen(Screen parentScreen) {
        if (guiBuilder == null) {
            if (GuiBuilder.DEFAULT != null) {
                guiBuilder = GuiBuilder.DEFAULT;
            } else {
                throw new UnsupportedOperationException("No GUI builder provided");
            }
        }
        return guiBuilder.buildScreen(parentScreen, config, this::save);
    }

    /**
     * Saves the config to a save file.
     */
    public void save() {
        CommentedConfigurationNode root = loader.createNode();
        config.fetch(root);
        try {
            loader.save(root);
        } catch (ConfigurateException e) {
            LOGGER.error("[CompleteConfig] Failed to save config to file!", e);
        }
    }

}