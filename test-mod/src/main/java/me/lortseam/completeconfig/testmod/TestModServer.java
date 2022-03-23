package me.lortseam.completeconfig.testmod;

import me.lortseam.completeconfig.testmod.config.Settings;
import net.fabricmc.api.DedicatedServerModInitializer;

public class TestModServer implements DedicatedServerModInitializer {

    private static Settings settings;

    @Override
    public void onInitializeServer() {
        settings = new Settings();
        settings.load();
    }

    public static Settings getSettings() {
        return settings;
    }
    
}
