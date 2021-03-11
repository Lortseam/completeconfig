package me.lortseam.completeconfig.example;

import lombok.Getter;
import me.lortseam.completeconfig.data.Config;
import net.fabricmc.api.ModInitializer;

public class ExampleMod implements ModInitializer {

    public static final String MOD_ID = "example";
    @Getter
    private static Config config;

    @Override
    public void onInitialize() {
        config = Config.builder(MOD_ID)
                .add(new Settings())
                .build();
    }

}
