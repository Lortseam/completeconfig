package me.lortseam.completeconfig;

import me.lortseam.completeconfig.data.Config;
import me.lortseam.completeconfig.gui.GuiBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

import java.util.HashSet;
import java.util.Set;

public final class ConfigHandler {

    private static final Set<ConfigHandler> handlers = new HashSet<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (ConfigHandler handler : handlers) {
                handler.save();
            }
        }));
    }

    private final Config config;
    private GuiBuilder guiBuilder;

    public ConfigHandler(Config config, GuiBuilder guiBuilder) {
        this.config = config;
        this.guiBuilder = guiBuilder;
        handlers.add(this);
        config.load();
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
                guiBuilder = GuiBuilder.DEFAULT.get();
            } else {
                throw new UnsupportedOperationException("No GUI builder provided");
            }
        }
        return guiBuilder.buildScreen(parentScreen, config, this::save);
    }

    /**
     * Saves the config to the dedicated save file.
     */
    public void save() {
        config.save();
    }

}