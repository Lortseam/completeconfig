package me.lortseam.completeconfig.gui;

import lombok.NonNull;
import me.lortseam.completeconfig.data.Config;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public abstract class ConfigScreenBuilder {

    private static final Map<String, ConfigScreenBuilder> builders = new HashMap<>();

    /**
     * Sets the main screen builder for a mod. The main screen builder will be used to build the config screen if no
     * custom builder was specified.
     *
     * @param modId the mod's ID
     * @param screenBuilder the screen builder
     */
    public static void setMain(@NonNull String modId, @NonNull ConfigScreenBuilder screenBuilder) {
        builders.put(modId, screenBuilder);
    }

    public static Optional<ConfigScreenBuilder> getMain(String modId) {
        return Optional.ofNullable(builders.get(modId));
    }

    /**
     * Builds a screen based on a config.
     *
     * @param parentScreen the parent screen
     * @param config the config to build the screen of
     * @return the built screen
     */
    public abstract Screen build(Screen parentScreen, Config config);

}
