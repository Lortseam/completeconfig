package me.lortseam.completeconfig;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import me.lortseam.completeconfig.extensions.CompleteConfigExtension;
import me.lortseam.completeconfig.extensions.clothbasicmath.ClothBasicMathExtension;
import me.lortseam.completeconfig.extensions.confabricate.ConfabricateExtension;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public final class CompleteConfig {

    private static final Map<String, CompleteConfigExtension> extensions = new HashMap<>();

    static {
        registerExternalExtension("cloth-basic-math", ClothBasicMathExtension.class);
        registerExternalExtension("confabricate", ConfabricateExtension.class);
        for (EntrypointContainer<CompleteConfigExtension> entrypoint : FabricLoader.getInstance().getEntrypointContainers("completeconfig-extension", CompleteConfigExtension.class)) {
            extensions.put(entrypoint.getProvider().getMetadata().getId(), entrypoint.getEntrypoint());
        }
    }

    public static void registerExternalExtension(@NonNull String modID, @NonNull Class<? extends CompleteConfigExtension> extensionClass) {
        if(!FabricLoader.getInstance().isModLoaded(modID)) return;
        try {
            Constructor<? extends CompleteConfigExtension> constructor = extensionClass.getDeclaredConstructor();
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }
            extensions.put(modID, constructor.newInstance());
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            logger.error("[CompleteConfig] Failed to instantiate extension " + modID, e);
        }
    }

    public static Collection<CompleteConfigExtension> getExtensions() {
        return Collections.unmodifiableCollection(extensions.values());
    }

}
