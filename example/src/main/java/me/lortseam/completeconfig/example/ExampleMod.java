package me.lortseam.completeconfig.example;

import me.lortseam.completeconfig.gui.ConfigScreenBuilder;
import me.lortseam.completeconfig.gui.cloth.ClothConfigScreenBuilder;
import net.fabricmc.api.ModInitializer;

public class ExampleMod implements ModInitializer {

    public static final String MOD_ID = "example";
    private static Settings settings;

    public static Settings getSettings() {
        return settings;
    }

    @Override
    public void onInitialize() {
        settings = new Settings();
        ConfigScreenBuilder.setMain(ExampleMod.MOD_ID, new ClothConfigScreenBuilder());
    }

}
