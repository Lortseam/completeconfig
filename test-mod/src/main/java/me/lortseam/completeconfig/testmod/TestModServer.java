package me.lortseam.completeconfig.testmod;

import me.lortseam.completeconfig.testmod.config.TestSettings;
import net.fabricmc.api.DedicatedServerModInitializer;

public class TestModServer implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        TestSettings settings = new TestSettings();
        settings.load();
    }
    
}
