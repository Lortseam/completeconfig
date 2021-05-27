package me.lortseam.completeconfig.data;

import com.google.common.collect.Lists;
import lombok.experimental.UtilityClass;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.data.transform.Transformation;
import me.lortseam.completeconfig.io.ConfigSource;
import me.lortseam.completeconfig.util.ReflectionUtils;
import net.minecraft.text.TextColor;

import java.util.*;
import java.util.Collection;

@UtilityClass
public final class ConfigRegistry {

    private static final Set<ConfigSource> sources = new HashSet<>();
    private static final Map<String, Config> mainConfigs = new HashMap<>();
    private static final Set<EntryOrigin> origins = new HashSet<>();
    private static final List<Transformation> transformations = Lists.newArrayList(
            Transformation.builder().byType(boolean.class, Boolean.class).byAnnotation(ConfigEntry.Boolean.class, true).transforms(BooleanEntry::new),
            Transformation.builder().byType(int.class, Integer.class).byAnnotation(ConfigEntry.BoundedInteger.class).transforms(origin -> {
                ConfigEntry.BoundedInteger bounds = origin.getAnnotation(ConfigEntry.BoundedInteger.class);
                return new BoundedEntry<>(origin, bounds.min(), bounds.max());
            }),
            Transformation.builder().byType(int.class, Integer.class).byAnnotation(Arrays.asList(ConfigEntry.BoundedInteger.class, ConfigEntry.Slider.class)).transforms(origin -> {
                ConfigEntry.BoundedInteger bounds = origin.getAnnotation(ConfigEntry.BoundedInteger.class);
                return new SliderEntry<>(origin, bounds.min(), bounds.max());
            }),
            Transformation.builder().byType(long.class, Long.class).byAnnotation(ConfigEntry.BoundedLong.class).transforms(origin -> {
                ConfigEntry.BoundedLong bounds = origin.getAnnotation(ConfigEntry.BoundedLong.class);
                return new BoundedEntry<>(origin, bounds.min(), bounds.max());
            }),
            Transformation.builder().byType(long.class, Long.class).byAnnotation(Arrays.asList(ConfigEntry.BoundedLong.class, ConfigEntry.Slider.class)).transforms(origin -> {
                ConfigEntry.BoundedLong bounds = origin.getAnnotation(ConfigEntry.BoundedLong.class);
                return new SliderEntry<>(origin, bounds.min(), bounds.max());
            }),
            Transformation.builder().byType(float.class, Float.class).byAnnotation(ConfigEntry.BoundedFloat.class).transforms(origin -> {
                ConfigEntry.BoundedFloat bounds = origin.getAnnotation(ConfigEntry.BoundedFloat.class);
                return new BoundedEntry<>(origin, bounds.min(), bounds.max());
            }),
            Transformation.builder().byType(double.class, Double.class).byAnnotation(ConfigEntry.BoundedDouble.class).transforms(origin -> {
                ConfigEntry.BoundedDouble bounds = origin.getAnnotation(ConfigEntry.BoundedDouble.class);
                return new BoundedEntry<>(origin, bounds.min(), bounds.max());
            }),
            Transformation.builder().byType(type -> Enum.class.isAssignableFrom(ReflectionUtils.getTypeClass(type))).transforms(EnumEntry::new),
            Transformation.builder().byType(type -> Enum.class.isAssignableFrom(ReflectionUtils.getTypeClass(type))).byAnnotation(ConfigEntry.Dropdown.class).transforms(DropdownEntry::new),
            Transformation.builder().byAnnotation(ConfigEntry.Color.class).transforms(ColorEntry::new),
            Transformation.builder().byType(TextColor.class).transforms(origin -> new ColorEntry<>(origin, false))
    );

    static void register(Config config) {
        if (!sources.add(config.getSource())) {
            throw new UnsupportedOperationException("A config of " + config.getSource() + " already exists");
        }
        String modId = config.getMod().getId();
        if (!mainConfigs.containsKey(modId)) {
            mainConfigs.put(modId, config);
        } else {
            mainConfigs.remove(modId);
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

    public static void setMainConfig(Config config) {
        mainConfigs.put(config.getMod().getId(), config);
    }

    public static Map<String, Config> getMainConfigs() {
        return Collections.unmodifiableMap(mainConfigs);
    }

    static Collection<Transformation> getTransformations() {
        return Collections.unmodifiableCollection(transformations);
    }

}
