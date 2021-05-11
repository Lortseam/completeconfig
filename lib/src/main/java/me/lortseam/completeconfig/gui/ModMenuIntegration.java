package me.lortseam.completeconfig.gui;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.lortseam.completeconfig.data.Config;
import me.lortseam.completeconfig.data.Registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class ModMenuIntegration implements ModMenuApi {

    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        Map<String, ConfigScreenFactory<?>> factories = new HashMap<>();
        for (Map.Entry<String, Config> entry : Registry.getMainConfigs().entrySet()) {
            Optional<ConfigScreenBuilder> builder = Registry.getScreenBuilder(entry.getKey(), ConfigScreenBuilder.FALLBACK);
            if (!builder.isPresent()) continue;
            factories.put(entry.getKey(), parentScreen -> builder.get().build(parentScreen, entry.getValue()));
        }
        return factories;
    }

}
