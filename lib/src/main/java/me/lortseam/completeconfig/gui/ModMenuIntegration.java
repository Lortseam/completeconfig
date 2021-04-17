package me.lortseam.completeconfig.gui;

import com.google.common.collect.Maps;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.lortseam.completeconfig.data.Config;
import me.lortseam.completeconfig.gui.cloth.ClothConfigScreenBuilder;

import java.util.Map;

public final class ModMenuIntegration implements ModMenuApi {

    private final ConfigScreenBuilder defaultScreenBuilder = new ClothConfigScreenBuilder();

    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        return Maps.transformValues(Config.getMainConfigs(), config -> parentScreen -> {
            return ConfigScreenBuilder.getMain(config.getMod().getId()).orElse(defaultScreenBuilder).build(parentScreen, config);
        });
    }

}
