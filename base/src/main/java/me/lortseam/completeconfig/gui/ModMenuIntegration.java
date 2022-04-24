package me.lortseam.completeconfig.gui;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.lortseam.completeconfig.data.Config;
import me.lortseam.completeconfig.data.ConfigRegistry;

import java.util.HashMap;
import java.util.Map;

public final class ModMenuIntegration implements ModMenuApi {

    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        Map<String, ConfigScreenFactory<?>> factories = new HashMap<>();
        for (Map.Entry<String, Config> entry : ConfigRegistry.getMainConfigs().entrySet()) {
            ConfigScreenBuilder.getMain(entry.getKey()).ifPresent(supplier -> {
                factories.put(entry.getKey(), parentScreen -> supplier.get().build(parentScreen, entry.getValue()));
            });
        }
        return factories;
    }

}
