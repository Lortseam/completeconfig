package me.lortseam.completeconfig.testmod;

import me.lortseam.completeconfig.testmod.config.ClientSettings;
import me.lortseam.completeconfig.gui.ConfigScreenBuilder;
import me.lortseam.completeconfig.gui.cloth.ClothConfigScreenBuilder;
import net.fabricmc.api.ClientModInitializer;

public class TestModClient implements ClientModInitializer {

    private static ClientSettings settings;

    @Override
    public void onInitializeClient() {
        settings = new ClientSettings();
        settings.load();
        ConfigScreenBuilder.setMain(TestMod.MOD_ID, new ClothConfigScreenBuilder());
    }

    public static ClientSettings getSettings() {
        return settings;
    }

}
