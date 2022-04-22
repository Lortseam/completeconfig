package me.lortseam.completeconfig.testmod;

import me.lortseam.completeconfig.testmod.config.ModConfig;
import net.fabricmc.api.DedicatedServerModInitializer;

public class TestModServer implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        ModConfig config = new ModConfig();
        config.load();
    }
    
}
