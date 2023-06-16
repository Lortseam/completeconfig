package me.lortseam.completeconfig.testmod;

import lombok.Getter;
import me.lortseam.completeconfig.testmod.config.ModConfig;
import net.fabricmc.api.ModInitializer;

public class TestMod implements ModInitializer {

    public static final String MOD_ID = "completeconfig-test-mod";
    @Getter
    private static final ModConfig config = new ModConfig();

    @Override
    public void onInitialize() {
        config.load();
    }
    
}
