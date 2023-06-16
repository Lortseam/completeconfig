package me.lortseam.completeconfig.testmod.gui;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.lortseam.completeconfig.gui.ConfigScreenBuilder;
import me.lortseam.completeconfig.testmod.TestMod;
import me.lortseam.completeconfig.testmod.TestModClient;

public class ModMenuIntegration implements ModMenuApi {

    private static final ConfigScreenBuilder<?> configScreenBuilder = TestModClient.getScreenBuilderType().create();

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> configScreenBuilder.build(parent, TestMod.getConfig());
    }

}
