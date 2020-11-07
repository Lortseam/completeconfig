package me.lortseam.completeconfig.gui;

import me.lortseam.completeconfig.Config;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

@Environment(EnvType.CLIENT)
public interface GuiBuilder {

    Screen buildScreen(Screen parentScreen, Config config, Runnable savingRunnable);

}
