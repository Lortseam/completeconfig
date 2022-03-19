package me.lortseam.completeconfig.testmod;

import me.lortseam.completeconfig.testmod.config.Settings;
import net.fabricmc.api.DedicatedServerModInitializer;

public class TestModServer implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        Settings settings = new Settings();
        settings.load();
    }
    
}
