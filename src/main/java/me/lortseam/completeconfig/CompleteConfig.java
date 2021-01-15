package me.lortseam.completeconfig;

import me.lortseam.completeconfig.extensions.CompleteConfigExtension;
import me.lortseam.completeconfig.extensions.clothbasicmath.ClothBasicMathExtension;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class CompleteConfig {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<String, CompleteConfigExtension> extensions = new HashMap<>();

    static {
        registerExternalExtension("cloth-basic-math", ClothBasicMathExtension.class);
        for (EntrypointContainer<CompleteConfigExtension> entrypoint : FabricLoader.getInstance().getEntrypointContainers("completeconfig-extension", CompleteConfigExtension.class)) {
            extensions.put(entrypoint.getProvider().getMetadata().getId(), entrypoint.getEntrypoint());
        }
    }

    public static void registerExternalExtension(String modID, Class<? extends CompleteConfigExtension> extensionClass) {
        if(!FabricLoader.getInstance().isModLoaded(modID)) return;
        try {
            extensions.put(modID, extensionClass.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.warn("[CompleteConfig] Failed to instantiate extension " + modID, e);
        }
    }

    public static Collection<CompleteConfigExtension> getExtensions() {
        return Collections.unmodifiableCollection(extensions.values());
    }

}
