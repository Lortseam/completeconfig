package me.lortseam.completeconfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import me.lortseam.completeconfig.api.ConfigCategory;
import me.lortseam.completeconfig.gui.GuiBuilder;
import me.lortseam.completeconfig.gui.GuiRegistry;
import me.lortseam.completeconfig.serialization.CollectionSerializer;
import me.lortseam.completeconfig.serialization.EntrySerializer;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

public class ConfigManager {

    private final Path jsonPath;
    private final Config config;
    private final GuiBuilder guiBuilder;

    ConfigManager(String modID) {
        jsonPath = Paths.get(FabricLoader.getInstance().getConfigDir().toString(), modID + ".json");
        config = new Config(load());
        guiBuilder = new GuiBuilder(modID, config);
    }

    private JsonElement load() {
        if(Files.exists(jsonPath)) {
            try {
                return new Gson().fromJson(new FileReader(jsonPath.toString()), JsonElement.class);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
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

    public Screen buildScreen(Screen parent) {
        return guiBuilder.buildScreen(parent, this::save);
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
        try(Writer writer = new FileWriter(jsonPath.toString())) {
            new GsonBuilder()
                    .registerTypeAdapter(CollectionSerializer.TYPE, new CollectionSerializer())
                    .registerTypeAdapter(EntrySerializer.TYPE, new EntrySerializer())
                    .setPrettyPrinting()
                    .create()
                    .toJson(config, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}