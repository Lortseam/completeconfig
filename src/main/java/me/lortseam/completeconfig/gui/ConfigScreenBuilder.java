package me.lortseam.completeconfig.gui;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.lortseam.completeconfig.data.Config;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ConfigScreenBuilder {

    protected final Config config;

    /**
     * Builds the screen.
     *
     * @param parentScreen the parent screen
     * @return the built screen
     */
    @Environment(EnvType.CLIENT)
    public abstract Screen build(Screen parentScreen);

}
