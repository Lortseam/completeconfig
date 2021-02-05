package me.lortseam.completeconfig.gui;

import me.lortseam.completeconfig.data.Config;
import me.lortseam.completeconfig.gui.cloth.ClothConfigScreenBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;

import java.util.Optional;

public interface ConfigScreenBuilder {

    /**
     * Returns a new instance of the default screen builder, if present.
     *
     * <p>More specifically, if installed, the {@link ClothConfigScreenBuilder} is used by default.
     *
     * @return the default screen builder or an empty value if absent
     */
    static Optional<ConfigScreenBuilder> getDefault() {
        if (FabricLoader.getInstance().isModLoaded("cloth-config2")) {
            return Optional.of(new ClothConfigScreenBuilder());
        }
        return Optional.empty();
    }

    /**
     * Builds a screen based on a config.
     *
     * @param parentScreen the parent screen
     * @param config the config to build the screen of
     * @return the built screen
     */
    @Environment(EnvType.CLIENT)
    Screen build(Screen parentScreen, Config config);

}
