package me.lortseam.completeconfig.testmod;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.lortseam.completeconfig.gui.ConfigScreenBuilder;
import me.lortseam.completeconfig.gui.cloth.ClothConfigScreenBuilder;

public final class ModMenuIntegration implements ModMenuApi {

    private final ConfigScreenBuilder screenBuilder = new ClothConfigScreenBuilder();

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> screenBuilder.build(parent, TestMod.getConfig());
    }

}
