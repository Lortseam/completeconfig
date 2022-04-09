package me.lortseam.completeconfig;

import com.google.common.collect.Sets;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import me.lortseam.completeconfig.data.extension.BaseExtension;
import me.lortseam.completeconfig.data.extension.ClientExtension;
import me.lortseam.completeconfig.data.extension.ServerExtension;
import me.lortseam.completeconfig.extensions.clothbasicmath.ClothBasicMathExtension;
import me.lortseam.completeconfig.extensions.clothconfig.ClothConfigClientExtension;
import me.lortseam.completeconfig.extensions.minecraft.MinecraftClientExtension;
import me.lortseam.completeconfig.util.ReflectionUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j(topic = "CompleteConfig")
@UtilityClass
public final class CompleteConfig {

    private static final Set<Class<? extends Extension>> validExtensionTypes = Sets.newHashSet(BaseExtension.class);
    private static final Set<Extension> extensions = new HashSet<>();

    static {
        registerExtensionType(ClientExtension.class, EnvType.CLIENT);
        registerExtensionType(ServerExtension.class, EnvType.SERVER);
        for (EntrypointContainer<CompleteConfigInitializer> entrypoint : FabricLoader.getInstance().getEntrypointContainers("completeconfig", CompleteConfigInitializer.class)) {
            entrypoint.getEntrypoint().onInitializeCompleteConfig();
        }
        registerExtension(MinecraftClientExtension.class);
        registerExtension("cloth-basic-math", ClothBasicMathExtension.class);
        registerExtension("cloth-config", ClothConfigClientExtension.class);
        for (EntrypointContainer<BaseExtension> entrypoint : FabricLoader.getInstance().getEntrypointContainers("completeconfig-extension", BaseExtension.class)) {
            registerExtension(entrypoint.getEntrypoint());
        }
    }

    /**
     * Registers a custom extension type which depends on the environment type and a list of loaded mods.
     *
     * @param extensionType the extension type
     * @param environment the required environment type
     * @param mods the required mods
     */
    public static void registerExtensionType(@NonNull Class<? extends Extension> extensionType, EnvType environment, String... mods) {
        if(validExtensionTypes.contains(extensionType)) return;
        if(environment != null && FabricLoader.getInstance().getEnvironmentType() != environment || Arrays.stream(mods).anyMatch(modId -> {
            return !FabricLoader.getInstance().isModLoaded(Objects.requireNonNull(modId));
        })) return;
        validExtensionTypes.add(extensionType);
    }

    /**
     * Registers a custom extension type which depends on a list of loaded mods.
     *
     * @param extensionType the extension type
     * @param mods the required mods
     */
    public static void registerExtensionType(@NonNull Class<? extends Extension> extensionType, String... mods) {
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

    static void registerExtension(Class<? extends Extension> extension) {
        if(!validExtensionTypes.containsAll(Arrays.asList(extension.getInterfaces()))) return;
        try {
            registerExtension(ReflectionUtils.instantiateClass(extension));
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            logger.error("Failed to instantiate extension " + extension, e);
        }
    }

    /**
     * Registers an external CompleteConfig extension. To register an extension provided by your own mod, use the
     * {@link BaseExtension} entrypoint.
     *
     * @param modId the ID of the external mod
     * @param extension the extension
     *
     * @see BaseExtension
     */
    public static void registerExtension(@NonNull String modId, @NonNull Class<? extends Extension> extension) {
        if(!FabricLoader.getInstance().isModLoaded(modId)) return;
        registerExtension(extension);
    }

    public static <E extends Extension, T> Collection<T> collectExtensions(Class<E> extensionType, Function<E, T> function) {
        return extensions.stream().filter(extensionType::isInstance).map(extension -> function.apply((E) extension)).filter(Objects::nonNull).collect(Collectors.toSet());
    }

}
