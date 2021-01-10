package me.lortseam.completeconfig.gui;

import me.lortseam.completeconfig.data.Config;
import me.lortseam.completeconfig.gui.cloth.ClothGuiBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;

public interface GuiBuilder {

    @Environment(EnvType.CLIENT)
    GuiBuilder DEFAULT = FabricLoader.getInstance().isModLoaded("cloth-config2") ? new ClothGuiBuilder() : null;

    @Environment(EnvType.CLIENT)
    Screen buildScreen(Screen parentScreen, Config config, Runnable savingRunnable);

}
