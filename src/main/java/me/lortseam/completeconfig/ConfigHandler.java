package me.lortseam.completeconfig;

import com.google.gson.*;
import me.lortseam.completeconfig.api.ConfigCategory;
import me.lortseam.completeconfig.api.ConfigOwner;
import me.lortseam.completeconfig.gui.GuiBuilder;
import me.lortseam.completeconfig.gui.cloth.ClothGuiBuilder;
import me.lortseam.completeconfig.serialization.CollectionSerializer;
import me.lortseam.completeconfig.serialization.EntrySerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public final class ConfigHandler {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(CollectionSerializer.TYPE, new CollectionSerializer())
            .registerTypeAdapter(EntrySerializer.TYPE, new EntrySerializer())
            .setPrettyPrinting()
            .create();
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<Class<? extends ConfigOwner>, ConfigHandler> HANDLERS = new HashMap<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (ConfigHandler handler : HANDLERS.values()) {
                handler.save();
            }
        }));
    }

    static ConfigHandler registerConfig(String modID, String[] branch, Class<? extends ConfigOwner> owner, List<ConfigCategory> topLevelCategories, GuiBuilder guiBuilder) {
        if (HANDLERS.containsKey(owner)) {
            throw new IllegalArgumentException("The specified owner " + owner + " already created a config!");
        }
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && guiBuilder == null) {
            if (FabricLoader.getInstance().isModLoaded("cloth-config2")) {
                guiBuilder = new ClothGuiBuilder();
            } else {
                throw new UnsupportedOperationException("No GUI builder provided");
            }
        }
        if (topLevelCategories.isEmpty()) {
            LOGGER.warn("[CompleteConfig] Owner " + owner + " of mod " + modID + " tried to create an empty config!");
            return null;
        }
        String[] subPath = ArrayUtils.add(branch, 0, modID);
        subPath[subPath.length - 1] = subPath[subPath.length - 1] + ".json";
        Path jsonPath = Paths.get(FabricLoader.getInstance().getConfigDir().toString(), subPath);
        if (HANDLERS.values().stream().anyMatch(handler -> handler.jsonPath.equals(jsonPath))) {
            throw new IllegalArgumentException("A config of the mod " + modID + " with the specified branch " + Arrays.toString(branch) + " already exists!");
        }
        ConfigHandler handler = new ConfigHandler(modID, jsonPath, topLevelCategories, guiBuilder);
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

    private final Path jsonPath;
    private final Config config;
    private final GuiBuilder guiBuilder;

    private ConfigHandler(String modID, Path jsonPath, List<ConfigCategory> topLevelCategories, GuiBuilder guiBuilder) {
        this.jsonPath = jsonPath;
        config = new Config(modID, topLevelCategories, load());
        this.guiBuilder = guiBuilder;

    }

    private JsonElement load() {
        if(Files.exists(jsonPath)) {
            try {
                return GSON.fromJson(new FileReader(jsonPath.toString()), JsonElement.class);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (JsonSyntaxException e) {
                LOGGER.warn("[CompleteConfig] An error occurred while trying to load the config " + jsonPath.toString());
            }
        }
        return JsonNull.INSTANCE;
    }

    /**
     * Generates the configuration GUI.
     *
     * @param parentScreen The parent screen
     * @return The generated configuration screen
     */
    @Environment(EnvType.CLIENT)
    public Screen buildScreen(Screen parentScreen) {
        return guiBuilder.buildScreen(parentScreen, config, this::save);
    }

    /**
     * Saves the config to a save file.
     */
    public void save() {
        if (!Files.exists(jsonPath)) {
            try {
                Files.createDirectories(jsonPath.getParent());
                Files.createFile(jsonPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try(Writer writer = Files.newBufferedWriter(jsonPath)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}