package me.lortseam.completeconfig;

import me.lortseam.completeconfig.gui.GuiBuilder;
import me.lortseam.completeconfig.gui.cloth.ClothGuiBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;

import java.util.Objects;

public final class ClientConfigManager extends ConfigManager {

    private GuiBuilder guiBuilder;

    ClientConfigManager(String modID) {
        super(modID);
    }

    /**
     * Sets a custom GUI builder.
     * @param guiBuilder The GUI builder for the mod's config
     */
    public void setGuiBuilder(GuiBuilder guiBuilder) {
        Objects.requireNonNull(guiBuilder);
        this.guiBuilder = guiBuilder;
    }

    /**
     * Generates the configuration GUI.
     * @param parentScreen The parent screen
     * @return The generated configuration screen
     */
    public Screen buildScreen(Screen parentScreen) {
        if (guiBuilder == null) {
            if (FabricLoader.getInstance().isModLoaded("cloth-config2")) {
                guiBuilder = new ClothGuiBuilder();
            } else {
                throw new UnsupportedOperationException("No GUI builder provided");
            }
        }
        return guiBuilder.buildScreen(parentScreen, config, this::save);
    }

}
