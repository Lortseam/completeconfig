package me.lortseam.completeconfig.gui;

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

    public static void setMain(String modID, ConfigScreenBuilder screenBuilder) {
        mainBuilders.put(modID, screenBuilder);
    }

    public static Optional<ConfigScreenBuilder> getMain(String modID) {
        if (mainBuilders.containsKey(modID)) {
            return Optional.of(mainBuilders.get(modID));
        }
        if (fallback != null) {
            return Optional.of(fallback);
        }
        return Optional.empty();
    }

    public static Optional<Screen> tryBuild(Screen parentScreen, Config config) {
        return getMain(config.getMod().getId()).map(builder -> builder.build(parentScreen, config));
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
