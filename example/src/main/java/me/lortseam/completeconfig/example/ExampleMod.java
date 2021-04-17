package me.lortseam.completeconfig.example;

import me.lortseam.completeconfig.data.Config;
import net.fabricmc.api.ModInitializer;

public class ExampleMod implements ModInitializer {

    public static final String MOD_ID = "example";

    @Override
    public void onInitialize() {
        Config.builder(MOD_ID)
                .add(new Settings())
                .build();
    }

}
