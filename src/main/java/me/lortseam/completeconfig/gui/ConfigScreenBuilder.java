package me.lortseam.completeconfig.gui;

import me.lortseam.completeconfig.data.Config;
import me.lortseam.completeconfig.gui.cloth.ClothConfigScreenBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;

import java.util.Optional;

public interface ConfigScreenBuilder {

    static Optional<ConfigScreenBuilder> getDefault() {
        if (FabricLoader.getInstance().isModLoaded("cloth-config2")) {
            return Optional.of(new ClothConfigScreenBuilder());
        }
        return Optional.empty();
    }

    @Environment(EnvType.CLIENT)
    Screen build(Screen parentScreen, Config config);

}
