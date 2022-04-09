package me.lortseam.completeconfig.data;

import com.google.common.collect.Lists;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import me.lortseam.completeconfig.data.transform.Transformation;

import java.util.*;

@UtilityClass
public final class ConfigRegistry {

    private static final Set<Config> configs = new HashSet<>();
    private static final Map<String, Config> mainConfigs = new HashMap<>();
    private static final Set<EntryOrigin> origins = new HashSet<>();
    private static final List<Transformation> transformations = Lists.newArrayList(Transformation.DEFAULTS);

    static void register(Config config) {
        if (!configs.add(config)) {
            throw new UnsupportedOperationException(config + " already exists");
        }
        String modId = config.getMod().getId();
        if (!mainConfigs.containsKey(modId)) {
            mainConfigs.put(modId, config);
        } else {
            mainConfigs.put(modId, null);
        }
    }

    static void register(EntryOrigin origin) {
        if (origins.contains(origin)) {
            throw new UnsupportedOperationException(origin.getField() + " was already resolved");
        }
        origins.add(origin);
    }

    static void register(Transformation... transformations) {
        ConfigRegistry.transformations.addAll(Arrays.asList(transformations));
    }

    /**
     * Sets the main config for a mod.
     *
     * <p>If a mod has only one config registered, that config is the main one. Therefore, setting the main config is
     * only required when a mod has two or more configs.
     *
     * @param config the main config
     */
    public static void setMainConfig(@NonNull Config config) {
        mainConfigs.put(config.getMod().getId(), config);
    }

    public static Map<String, Config> getMainConfigs() {
        return Collections.unmodifiableMap(mainConfigs);
    }

    static Collection<Transformation> getTransformations() {
        return Collections.unmodifiableCollection(transformations);
    }

}
