package me.lortseam.completeconfig.data;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import me.lortseam.completeconfig.CompleteConfig;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.data.structure.Identifiable;
import me.lortseam.completeconfig.data.structure.StructurePart;
import me.lortseam.completeconfig.data.structure.client.TooltipSupplier;
import me.lortseam.completeconfig.data.structure.client.Translatable;
import me.lortseam.completeconfig.data.transform.Transformation;
import me.lortseam.completeconfig.data.transform.Transformer;
import me.lortseam.completeconfig.data.extension.BaseExtension;
import me.lortseam.completeconfig.text.TranslationKey;
import me.lortseam.completeconfig.util.ReflectionUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;

@Slf4j(topic = "CompleteConfig")
public class Entry<T> implements StructurePart, Identifiable, Translatable, TooltipSupplier {

    private static final Transformer DEFAULT_TRANSFORMER = Entry::new;

    static {
        for (Transformation[] transformations : CompleteConfig.collectExtensions(BaseExtension.class, BaseExtension::getTransformations)) {
            ConfigRegistry.register(transformations);
        }
    }

    static Entry<?> of(Config root, Parent parent, Field field, ConfigContainer object) {
        EntryOrigin origin = new EntryOrigin(root, parent, field, object);
        return ConfigRegistry.getTransformations().stream().filter(transformation -> {
            return transformation.test(origin);
        }).findFirst().map(Transformation::getTransformer).orElse(DEFAULT_TRANSFORMER).transform(origin);
    }

    protected final EntryOrigin origin;
    @Getter
    private final Class<T> typeClass;
    @Getter
    private final String id;
    @Getter
    private final T defaultValue;
    @Environment(EnvType.CLIENT)
    private TranslationKey translation;
    @Environment(EnvType.CLIENT)
    private TranslationKey[] tooltipTranslation;
    @Accessors(fluent = true)
    @Getter
    private final boolean requiresRestart;
    private final String comment;
    private final UnaryOperator<T> valueModifier;
    private final Setter<T> setter;

    protected Entry(EntryOrigin origin, UnaryOperator<T> valueModifier) {
        ConfigRegistry.register(origin);
        this.origin = origin;
        if (!origin.getField().canAccess(origin.getObject())) {
            origin.getField().setAccessible(true);
        }
        typeClass = (Class<T>) ReflectionUtils.getTypeClass(getType());
        this.valueModifier = valueModifier;
        defaultValue = getValue();
        Optional<ConfigEntry> annotation = origin.getOptionalAnnotation(ConfigEntry.class);
        id = annotation.isPresent() && !annotation.get().value().isBlank() ? annotation.get().value() : origin.getField().getName();
        requiresRestart = annotation.isPresent() && annotation.get().requiresRestart();
        comment = annotation.isPresent() && !annotation.get().comment().isBlank() ? annotation.get().comment() : null;
        setter = ReflectionUtils.getSetterMethod(origin.getField(), origin.getObject()).<Setter<T>>map(method -> method::invoke).orElse((object, value) -> origin.getField().set(object, value));
    }

    protected Entry(EntryOrigin origin) {
        this(origin, null);
    }

    public Type getType() {
        return origin.getType();
    }

    public T getValue() {
        if (update()) {
            return getValue();
        }
        return getFieldValue();
    }

    private T getFieldValue() {
        try {
            return (T) Objects.requireNonNull(origin.getField().get(origin.getObject()), origin.getField().toString());
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
        origin.getObject().onUpdate();
        origin.getRoot().onChildUpdate();
        return true;
    }

    private void set(T value) {
        try {
            setter.set(origin.getObject(), value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to set entry value", e);
        }
    }

    @Override
    public TranslationKey getTranslation() {
        if (translation == null) {
            Optional<ConfigEntry> annotation = origin.getOptionalAnnotation(ConfigEntry.class);
            if (annotation.isPresent() && !annotation.get().translationKey().isBlank()) {
                translation = origin.getParent().getTranslation().root().append(annotation.get().translationKey());
            } else {
                translation = origin.getParent().getTranslation().append(id);
            }
        }
        return translation;
    }

    @Override
    public TranslationKey[] getTooltipTranslation() {
        if (tooltipTranslation == null) {
            Optional<ConfigEntry> annotation = origin.getOptionalAnnotation(ConfigEntry.class);
            if (annotation.isPresent() && annotation.get().tooltipTranslationKeys().length > 0) {
                tooltipTranslation = Arrays.stream(annotation.get().tooltipTranslationKeys()).map(key -> {
                    if (key.isBlank()) {
                        throw new AssertionError("Tooltip translation key of entry " + origin.getField() + " may not be blank");
                    }
                    return getTranslation().root().append(key);
                }).toArray(TranslationKey[]::new);
            } else {
                tooltipTranslation = getTranslation().appendTooltip().orElse(new TranslationKey[0]);
            }
        }
        return tooltipTranslation;
    }

    @Override
    public void apply(CommentedConfigurationNode node) {
        try {
            T value = (T) node.get(getType());
            if (value == null) {
                throw new SerializationException(node, getType(), "Unable to deserialize value of this type");
            }
            setValue(value);
        } catch (SerializationException e) {
            logger.error("Failed to apply value to entry", e);
        }
    }

    @Override
    public void fetch(CommentedConfigurationNode node) {
        try {
            // Need to box the type here as getValue returns the boxed value
            node.set(ReflectionUtils.boxType(getType()), getValue());
            if (comment != null) {
                node.comment(comment);
            }
        } catch (SerializationException e) {
            logger.error("Failed to fetch value from entry", e);
        }
    }

    @Override
    public String toString() {
        return origin.getField().toString();
    }

    @FunctionalInterface
    private interface Setter<T> {

        void set(ConfigContainer object, T value) throws IllegalAccessException, InvocationTargetException;

    }

}