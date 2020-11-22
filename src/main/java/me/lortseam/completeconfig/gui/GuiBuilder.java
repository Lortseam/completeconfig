package me.lortseam.completeconfig.gui;

import me.lortseam.completeconfig.Config;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

public interface GuiBuilder {

    @Environment(EnvType.CLIENT)
    Screen buildScreen(Screen parentScreen, Config config, Runnable savingRunnable);

}
