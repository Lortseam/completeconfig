package me.lortseam.completeconfig;

import com.google.gson.*;
import me.lortseam.completeconfig.api.ConfigCategory;
import me.lortseam.completeconfig.serialization.CollectionSerializer;
import me.lortseam.completeconfig.serialization.EntrySerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
public abstract class ConfigHandler {

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
     * Gets the {@link ConfigHandler} for the specified mod if that mod was registered before.
     *
     * @param modID The ID of the mod
     * @return The {@link ConfigHandler} if one was found or else an empty result
     */
    public static Optional<ConfigHandler> of(String modID) {
        return CompleteConfig.getManager(modID);
    }

    /**
     * Gets the {@link ClientConfigHandler} for the specified mod if that mod was registered before.
     *
     * @param modID The ID of the mod
     * @return The {@link ClientConfigHandler} if one was found or else an empty result
     */
    @Environment(EnvType.CLIENT)
    public static Optional<ClientConfigHandler> ofClient(String modID) {
        return of(modID).map(manager -> (ClientConfigHandler) manager);
    }

    /**
     * Gets the {@link ServerConfigHandler} for the specified mod if that mod was registered before.
     *
     * @param modID The ID of the mod
     * @return The {@link ServerConfigHandler} if one was found or else an empty result
     */
    @Environment(EnvType.SERVER)
    public static Optional<ServerConfigHandler> ofServer(String modID) {
        return of(modID).map(manager -> (ServerConfigHandler) manager);
    }

    ConfigHandler(String modID) {
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