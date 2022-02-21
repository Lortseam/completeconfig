package me.lortseam.completeconfig.example;

import me.lortseam.completeconfig.example.config.Settings;
import net.fabricmc.api.DedicatedServerModInitializer;

public class ExampleModServer implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        Settings settings = new Settings();
        settings.load();
    }
    
}
