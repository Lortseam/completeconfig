package me.lortseam.completeconfig.testmod;

import me.lortseam.completeconfig.testmod.config.ModConfig;
import net.fabricmc.api.DedicatedServerModInitializer;

public class TestMod implements DedicatedServerModInitializer {

    public static final String MOD_ID = "completeconfig-test-mod";

    @Override
    public void onInitializeServer() {
        ModConfig config = new ModConfig();
        config.load();
    }
    
}
