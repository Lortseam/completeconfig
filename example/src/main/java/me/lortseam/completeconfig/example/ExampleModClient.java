package me.lortseam.completeconfig.example;

import me.lortseam.completeconfig.gui.ConfigScreenBuilder;
import me.lortseam.completeconfig.gui.cloth.ClothConfigScreenBuilder;
import net.fabricmc.api.ClientModInitializer;

public class ExampleModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ConfigScreenBuilder.setMain(ExampleMod.MOD_ID, new ClothConfigScreenBuilder());
    }

}
