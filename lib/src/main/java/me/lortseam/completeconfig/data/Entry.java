package me.lortseam.completeconfig.data;

import com.google.common.collect.Lists;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;
import me.lortseam.completeconfig.CompleteConfig;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.data.entry.EntryOrigin;
import me.lortseam.completeconfig.data.entry.Transformation;
import me.lortseam.completeconfig.data.entry.Transformer;
import me.lortseam.completeconfig.data.structure.DataPart;
import me.lortseam.completeconfig.data.structure.Identifiable;
import me.lortseam.completeconfig.data.text.TranslationKey;
import me.lortseam.completeconfig.exception.IllegalAnnotationParameterException;
import me.lortseam.completeconfig.extensions.CompleteConfigExtension;
import me.lortseam.completeconfig.util.ReflectionUtils;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.*;
import java.util.function.UnaryOperator;

@Log4j2(topic = "CompleteConfig")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Entry<T> implements DataPart, Identifiable {

    private static final Transformer DEFAULT_TRANSFORMER = Entry::new;
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
    private static final Set<Entry> entries = new HashSet<>();

    static {
        for (Collection<Transformation> transformations : CompleteConfig.collectExtensions(CompleteConfigExtension.class, CompleteConfigExtension::getTransformations)) {
            Entry.transformations.addAll(transformations);
        }
    }

    static Entry<?> of(Field field, ConfigContainer parentObject, TranslationKey parentTranslation) {
        EntryOrigin origin = new EntryOrigin(field, parentObject, parentTranslation);
        Entry<?> entry = transformations.stream().filter(transformation -> transformation.test(origin)).findFirst().map(Transformation::getTransformer).orElse(DEFAULT_TRANSFORMER).transform(origin);
        if (!entries.add(entry)) {
            throw new UnsupportedOperationException(entry + " was already resolved");
        }
        return entry;
    }

    @EqualsAndHashCode.Include
    private final Field field;
    @Getter
    private final Type type;
    @Getter
    private final Class<T> typeClass;
    @EqualsAndHashCode.Include
    private final ConfigContainer parentObject;
    @Getter
    private final String id;
    @Getter
    private final T defaultValue;
    protected final TranslationKey translation;
    private final TranslationKey[] tooltipTranslation;
    @Accessors(fluent = true)
    @Getter
    private final boolean requiresRestart;
    private final String comment;
    private final UnaryOperator<T> valueModifier;

    protected Entry(EntryOrigin origin, UnaryOperator<T> valueModifier) {
        field = origin.getField();
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        type = origin.getType();
        typeClass = (Class<T>) ReflectionUtils.getTypeClass(type);
        parentObject = origin.getParentObject();
        this.valueModifier = valueModifier;
        defaultValue = getValue();
        ConfigEntry annotation = field.getDeclaredAnnotation(ConfigEntry.class);
        id = annotation != null && !StringUtils.isBlank(annotation.value()) ? annotation.value() : field.getName();
        if (annotation != null && !StringUtils.isBlank(annotation.translationKey())) {
            translation = origin.getParentTranslation().append(annotation.translationKey());
        } else {
            translation = origin.getParentTranslation().append(id);
        }
        if (annotation != null && annotation.tooltipTranslationKeys().length > 0) {
            tooltipTranslation = Arrays.stream(annotation.tooltipTranslationKeys()).map(key -> {
                if (StringUtils.isBlank(key)) {
                    throw new IllegalAnnotationParameterException("Tooltip translation key of entry " + this + " may not be blank");
                }
                return translation.root().append(key);
            }).toArray(TranslationKey[]::new);
        } else {
            tooltipTranslation = translation.appendTooltip().orElse(null);
        }
        requiresRestart = annotation != null && annotation.requiresRestart();
        comment = annotation != null && !StringUtils.isBlank(annotation.comment()) ? annotation.comment() : null;
    }

    protected Entry(EntryOrigin origin) {
        this(origin, null);
    }

    public T getValue() {
        if (update()) {
            return getValue();
        }
        return getFieldValue();
    }

    private T getFieldValue() {
        try {
            return (T) Objects.requireNonNull(field.get(parentObject), field.toString());
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to get entry value", e);
        }
    }

    public void setValue(@NonNull T value) {
        update(value);
    }

    private boolean update() {
        return update(getFieldValue());
    }

    private boolean update(T value) {
        if (valueModifier != null) {
            value = valueModifier.apply(value);
        }
        if (value.equals(getFieldValue())) {
            return false;
        }
        set(value);
        return true;
    }

    private void set(T value) {
        try {
            Optional<Method> writeMethod = ReflectionUtils.getWriteMethod(field);
            if (writeMethod.isPresent()) {
                writeMethod.get().invoke(parentObject, value);
            } else {
                field.set(parentObject, value);
            }
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to set entry value", e);
        }
    }

    public Text getText() {
        return translation.toText();
    }

    public Optional<Text[]> getTooltip() {
        return Optional.ofNullable(tooltipTranslation).map(lines -> {
            return Arrays.stream(lines).map(TranslationKey::toText).toArray(Text[]::new);
        });
    }

    @Override
    public void apply(CommentedConfigurationNode node) {
        try {
            T value = (T) node.get(type);
            // value could be null despite the virtual() check
            // see https://github.com/SpongePowered/Configurate/issues/187
            if(value == null) return;
            setValue(value);
        } catch (SerializationException e) {
            logger.error("Failed to apply value to entry", e);
        }
    }

    @Override
    public void fetch(CommentedConfigurationNode node) {
        try {
            node.set(type, getValue());
            if (comment != null) {
                node.comment(comment);
            }
        } catch (SerializationException e) {
            logger.error("Failed to fetch value from entry", e);
        }
    }

    @Override
    public String toString() {
        return field.toString();
    }

}