package me.lortseam.completeconfig.gui;

import me.lortseam.completeconfig.data.Config;
import me.lortseam.completeconfig.gui.cloth.ClothConfigScreenBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;

import java.util.function.Supplier;

public interface ConfigScreenBuilder {

    @Environment(EnvType.CLIENT)
    Supplier<ConfigScreenBuilder> DEFAULT = FabricLoader.getInstance().isModLoaded("cloth-config2") ? ClothConfigScreenBuilder::new : null;

    @Environment(EnvType.CLIENT)
    Screen build(Screen parentScreen, Config config);

}
