package me.lortseam.completeconfig.testmod;

import me.lortseam.completeconfig.data.ConfigRegistry;
import me.lortseam.completeconfig.testmod.config.ClientTestSettings;
import me.lortseam.completeconfig.testmod.config.ClientOptions;
import me.lortseam.completeconfig.gui.ConfigScreenBuilder;
import net.fabricmc.api.ClientModInitializer;

public class TestModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        new ClientOptions().load();
        ClientTestSettings settings = new ClientTestSettings();
        settings.load();
        ConfigRegistry.setMainConfig(settings);
        ConfigScreenBuilder.setMain(TestMod.MOD_ID, () -> ClientOptions.getScreenBuilderType().create());
    }

}
