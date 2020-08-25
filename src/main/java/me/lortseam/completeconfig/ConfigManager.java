package me.lortseam.completeconfig;

import com.google.gson.*;
import me.lortseam.completeconfig.api.ConfigCategory;
import me.lortseam.completeconfig.gui.GuiBuilder;
import me.lortseam.completeconfig.gui.GuiRegistry;
import me.lortseam.completeconfig.serialization.CollectionSerializer;
import me.lortseam.completeconfig.serialization.EntrySerializer;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

public final class ConfigManager {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(CollectionSerializer.TYPE, new CollectionSerializer())
            .registerTypeAdapter(EntrySerializer.TYPE, new EntrySerializer())
            .setPrettyPrinting()
            .create();
    private static final Logger LOGGER = LogManager.getLogger();

    private final String modID;
    private final Path jsonPath;
    private final Config config;
    private final GuiBuilder guiBuilder;

    ConfigManager(String modID) {
        this.modID = modID;
        jsonPath = Paths.get(FabricLoader.getInstance().getConfigDir().toString(), modID + ".json");
        config = new Config(modID, load());
        guiBuilder = new GuiBuilder(this, config);
    }

    private JsonElement load() {
        if(Files.exists(jsonPath)) {
            try {
                return GSON.fromJson(new FileReader(jsonPath.toString()), JsonElement.class);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (JsonSyntaxException e) {
                LOGGER.warn("An error occurred while trying to load the config for mod " + modID);
            }
        }
        return JsonNull.INSTANCE;
    }

    public void register(ConfigCategory... categories) {
        for (ConfigCategory category : categories) {
            config.registerTopLevelCategory(category);
        }
    }

    public GuiRegistry getGuiRegistry() {
        return guiBuilder.getRegistry();
    }

    public void setCustomGuiSupplier(Supplier<ConfigBuilder> supplier) {
        guiBuilder.setSupplier(supplier);
    }

    /**
     * @deprecated Use {@link #setCustomGuiSupplier(Supplier)}.
     */
    @Deprecated
    public void setCustomGuiBuilder(Supplier<ConfigBuilder> guiBuilder) {
        setCustomGuiSupplier(guiBuilder);
    }

    public Screen buildScreen(Screen parentScreen) {
        return guiBuilder.buildScreen(parentScreen, this::save);
    }

    /**
     * @deprecated Use {@link #buildScreen(Screen)}.
     */
    @Deprecated
    public Screen getConfigScreen(Screen parentScreen) {
        return buildScreen(parentScreen);
    }

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