package me.lortseam.completeconfig.gui;

import lombok.NonNull;
import me.lortseam.completeconfig.data.Config;
import me.lortseam.completeconfig.gui.cloth.ClothConfigScreenBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class ConfigScreenBuilder {

    private static final ConfigScreenBuilder fallback = FabricLoader.getInstance().isModLoaded("cloth-config2") ? new ClothConfigScreenBuilder() : null;
    private static final Map<String, ConfigScreenBuilder> mainBuilders = new HashMap<>();

    /**
     * Sets the main screen builder for a mod. The main screen builder will be used to build the config screen if no
     * custom builder was specified.
     *
     * @param modID the mod's ID
     * @param screenBuilder the screen builder
     */
    public static void setMain(@NonNull String modID, @NonNull ConfigScreenBuilder screenBuilder) {
        mainBuilders.put(modID, screenBuilder);
    }

    public static Optional<ConfigScreenBuilder> getMain(@NonNull String modID) {
        if (mainBuilders.containsKey(modID)) {
            return Optional.of(mainBuilders.get(modID));
        }
        return Optional.ofNullable(fallback);
    }

    /**
     * Builds a screen based on a config.
     *
     * @param parentScreen the parent screen
     * @param config the config to build the screen of
     * @return the built screen
     */
    @Environment(EnvType.CLIENT)
    public abstract Screen build(Screen parentScreen, Config config);

}
