package me.lortseam.completeconfig;

import com.google.gson.*;
import me.lortseam.completeconfig.api.ConfigCategory;
import me.lortseam.completeconfig.serialization.CollectionSerializer;
import me.lortseam.completeconfig.serialization.EntrySerializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Main interaction class for using the CompleteConfig API. References a single mod.
 */
public abstract class ConfigManager {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(CollectionSerializer.TYPE, new CollectionSerializer())
            .registerTypeAdapter(EntrySerializer.TYPE, new EntrySerializer())
            .setPrettyPrinting()
            .create();
    private static final Logger LOGGER = LogManager.getLogger();

    private final String modID;
    private final Path jsonPath;
    protected final Config config;

    /**
     * Gets the {@link ConfigManager} for the specified mod if that mod was registered before.
     *
     * @param modID The ID of the mod
     * @return The {@link ConfigManager} if one was found or else an empty result
     */
    public static Optional<ConfigManager> of(String modID) {
        return CompleteConfig.getManager(modID);
    }

    ConfigManager(String modID) {
        this.modID = modID;
        jsonPath = Paths.get(FabricLoader.getInstance().getConfigDir().toString(), modID + ".json");
        config = new Config(modID, load());
    }

    private JsonElement load() {
        if(Files.exists(jsonPath)) {
            try {
                return GSON.fromJson(new FileReader(jsonPath.toString()), JsonElement.class);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (JsonSyntaxException e) {
                LOGGER.warn("[CompleteConfig] An error occurred while trying to load the config for mod " + modID);
            }
        }
        return JsonNull.INSTANCE;
    }

    /**
     * Registers one or more top level categories.
     * @param categories The categories to register
     */
    public void register(ConfigCategory... categories) {
        for (ConfigCategory category : categories) {
            config.registerTopLevelCategory(category);
        }
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