package me.lortseam.completeconfig.example;

import net.fabricmc.api.ModInitializer;

public class ExampleMod implements ModInitializer {

    public static final String MOD_ID = "example";

    @Override
    public void onInitialize() {
        new Settings();
    }

}
