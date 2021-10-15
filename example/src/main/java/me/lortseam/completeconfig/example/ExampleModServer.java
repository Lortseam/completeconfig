package me.lortseam.completeconfig.example;

import me.lortseam.completeconfig.example.config.Settings;
import net.fabricmc.api.DedicatedServerModInitializer;

public class ExampleModServer implements DedicatedServerModInitializer {

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
