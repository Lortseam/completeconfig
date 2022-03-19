package me.lortseam.completeconfig.testmod;

import me.lortseam.completeconfig.data.ConfigRegistry;
import me.lortseam.completeconfig.testmod.config.ClientSettings;
import me.lortseam.completeconfig.testmod.config.Options;
import me.lortseam.completeconfig.gui.ConfigScreenBuilder;
import net.fabricmc.api.ClientModInitializer;

public class TestModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        new Options().load();
        ClientSettings settings = new ClientSettings();
        settings.load();
        ConfigRegistry.setMainConfig(settings);
        ConfigScreenBuilder.setMain(TestMod.MOD_ID, () -> Options.getScreenBuilderType().create());
    }

}
