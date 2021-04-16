package me.lortseam.completeconfig;

import com.google.common.collect.Sets;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import me.lortseam.completeconfig.extensions.CompleteConfigExtension;
import me.lortseam.completeconfig.extensions.Extension;
import me.lortseam.completeconfig.extensions.GuiExtension;
import me.lortseam.completeconfig.extensions.clothbasicmath.ClothBasicMathExtension;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CompleteConfig implements Extension {

    private static final Set<Class<? extends Extension>> validExtensionTypes = Sets.newHashSet(CompleteConfigExtension.class);
    private static final Set<Extension> extensions = new HashSet<>();

    static {
        registerExtensionType(GuiExtension.class, EnvType.CLIENT, "cloth-config2");
        registerExternalExtension("cloth-basic-math", ClothBasicMathExtension.class);
        for (EntrypointContainer<CompleteConfigExtension> entrypoint : FabricLoader.getInstance().getEntrypointContainers("completeconfig-extension", CompleteConfigExtension.class)) {
            registerExtension(entrypoint.getEntrypoint());
        }
    }

    public static void registerExtensionType(Class<? extends Extension> extensionType, EnvType environment, String... mods) {
        if(validExtensionTypes.contains(extensionType)) return;
        if(environment != null && FabricLoader.getInstance().getEnvironmentType() != environment || Arrays.stream(mods).anyMatch(modID -> !FabricLoader.getInstance().isModLoaded(modID))) return;
        validExtensionTypes.add(extensionType);
    }

    public static void registerExtensionType(Class<? extends Extension> extensionType, String... mods) {
        registerExtensionType(extensionType, null, mods);
    }

    private static void registerExtension(Extension extension) {
        extensions.add(extension);
        Set<Class<? extends Extension>> children = extension.children();
        if(children == null) return;
        for (Class<? extends Extension> child : children) {
            registerExtension(child);
        }
    }

    private static void registerExtension(Class<? extends Extension> extension) {
        if(Collections.disjoint(ClassUtils.getAllInterfaces(extension), validExtensionTypes)) return;
        try {
            Constructor<? extends Extension> constructor = extension.getDeclaredConstructor();
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }
            registerExtension(constructor.newInstance());
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            logger.error("[CompleteConfig] Failed to instantiate extension " + extension, e);
        }
    }

    public static void registerExternalExtension(@NonNull String modID, @NonNull Class<? extends CompleteConfigExtension> extensionType) {
        if(!FabricLoader.getInstance().isModLoaded(modID)) return;
        registerExtension(extensionType);
    }

    public static Collection<Extension> getExtensions() {
        return Collections.unmodifiableCollection(extensions);
    }

}
