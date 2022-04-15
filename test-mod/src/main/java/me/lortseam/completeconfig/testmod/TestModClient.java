package me.lortseam.completeconfig.testmod;

import lombok.Getter;
import me.lortseam.completeconfig.testmod.config.ClientTestSettings;
import me.lortseam.completeconfig.gui.ConfigScreenBuilder;
import me.lortseam.completeconfig.testmod.gui.ScreenBuilderType;
import net.fabricmc.api.ClientModInitializer;

public class TestModClient implements ClientModInitializer {

    @Getter
    private static final ScreenBuilderType screenBuilderType = ScreenBuilderType.valueOf(System.getProperty("screenBuilderType", ScreenBuilderType.values()[0].name()));

    @Override
    public void onInitializeClient() {
        ClientTestSettings settings = new ClientTestSettings();
        settings.load();
        ConfigScreenBuilder.setMain(TestMod.MOD_ID, screenBuilderType.create());
    }

}
