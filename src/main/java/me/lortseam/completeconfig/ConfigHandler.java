package me.lortseam.completeconfig;

import me.lortseam.completeconfig.api.ConfigGroup;
import me.lortseam.completeconfig.data.Config;
import me.lortseam.completeconfig.gui.GuiBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ConfigHandler {

    private static final Set<ConfigHandler> HANDLERS = new HashSet<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (ConfigHandler handler : HANDLERS) {
                handler.save();
            }
        }));
    }

    private final ConfigSource source;
    private final Config config;
    private GuiBuilder guiBuilder;

    ConfigHandler(ConfigSource source, List<ConfigGroup> topLevelGroups, GuiBuilder guiBuilder) {
        this.source = source;
        config = new Config(source.getModID(), topLevelGroups);
        this.guiBuilder = guiBuilder;
        HANDLERS.add(this);
        source.load(config);
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
                guiBuilder = GuiBuilder.DEFAULT;
            } else {
                throw new UnsupportedOperationException("No GUI builder provided");
            }
        }
        return guiBuilder.buildScreen(parentScreen, config, this::save);
    }

    /**
     * Saves the config to a save file.
     */
    public void save() {
        source.save(config);
    }

}