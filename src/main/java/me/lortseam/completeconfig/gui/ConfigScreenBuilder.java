package me.lortseam.completeconfig.gui;

import me.lortseam.completeconfig.data.Config;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

public abstract class ConfigScreenBuilder {

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
