package me.lortseam.completeconfig.gui;

import me.lortseam.completeconfig.data.Config;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class ConfigScreenBuilder {

    private static final Map<String, ConfigScreenBuilder> mainBuilders = new HashMap<>();

    public static void setMainBuilder(String modID, ConfigScreenBuilder screenBuilder) {
        mainBuilders.put(modID, screenBuilder);
    }

    public static Optional<ConfigScreenBuilder> getMainBuilder(String modID) {
        return Optional.ofNullable(mainBuilders.get(modID));
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
