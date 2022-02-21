package me.lortseam.completeconfig.example;

import me.lortseam.completeconfig.data.ConfigRegistry;
import me.lortseam.completeconfig.example.config.ClientSettings;
import me.lortseam.completeconfig.example.config.Options;
import me.lortseam.completeconfig.gui.ConfigScreenBuilder;
import net.fabricmc.api.ClientModInitializer;

public class ExampleModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        new Options().load();
        ClientSettings settings = new ClientSettings();
        settings.load();
        ConfigRegistry.setMainConfig(settings);
        ConfigScreenBuilder.setMain(ExampleMod.MOD_ID, () -> Options.getScreenBuilderType().create());
    }

}
