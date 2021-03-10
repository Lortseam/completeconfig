package me.lortseam.completeconfig.testmod;

import lombok.Getter;
import me.lortseam.completeconfig.data.Config;
import net.fabricmc.api.ModInitializer;

public class TestMod implements ModInitializer {

    public static final String MOD_ID = "testmod";
    @Getter
    private static Config config;

    @Override
    public void onInitialize() {
        config = Config.builder(MOD_ID)
                .add(new Settings())
                .build();
    }

}
