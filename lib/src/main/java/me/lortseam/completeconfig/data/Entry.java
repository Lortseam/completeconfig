package me.lortseam.completeconfig.data;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;
import me.lortseam.completeconfig.CompleteConfig;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.data.transform.Transformation;
import me.lortseam.completeconfig.data.transform.Transformer;
import me.lortseam.completeconfig.data.structure.DataPart;
import me.lortseam.completeconfig.data.structure.Identifiable;
import me.lortseam.completeconfig.data.structure.TooltipSupplier;
import me.lortseam.completeconfig.text.TranslationKey;
import me.lortseam.completeconfig.exception.IllegalAnnotationParameterException;
import me.lortseam.completeconfig.extensions.CompleteConfigExtension;
import me.lortseam.completeconfig.util.ReflectionUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;

@Log4j2(topic = "CompleteConfig")
public class Entry<T> implements DataPart, Identifiable, TooltipSupplier {

    private static final Transformer DEFAULT_TRANSFORMER = Entry::new;

    static {
        for (Transformation[] transformations : CompleteConfig.collectExtensions(CompleteConfigExtension.class, CompleteConfigExtension::getTransformations)) {
            Registry.register(transformations);
        }
    }

    static Entry<?> of(BaseCollection parent, Field field, ConfigContainer object) {
        EntryOrigin origin = new EntryOrigin(parent, field, object);
        return Registry.getTransformations().stream().filter(transformation -> {
            return transformation.test(origin);
        }).findFirst().map(Transformation::getTransformer).orElse(DEFAULT_TRANSFORMER).transform(origin);
    }

    protected final EntryOrigin origin;
    @Getter
    private final Type type;
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

    protected Entry(EntryOrigin origin, UnaryOperator<T> valueModifier) {
        Registry.register(origin);
        this.origin = origin;
        if (!origin.getField().isAccessible()) {
            origin.getField().setAccessible(true);
        }
        type = origin.getType();
        typeClass = (Class<T>) ReflectionUtils.getTypeClass(type);
        this.valueModifier = valueModifier;
        defaultValue = getValue();
        Optional<ConfigEntry> annotation = origin.getOptionalAnnotation(ConfigEntry.class);
        id = annotation.isPresent() && !StringUtils.isBlank(annotation.get().value()) ? annotation.get().value() : origin.getField().getName();
        requiresRestart = annotation.isPresent() && annotation.get().requiresRestart();
        comment = annotation.isPresent() && !StringUtils.isBlank(annotation.get().comment()) ? annotation.get().comment() : null;
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
        return true;
    }

    private void set(T value) {
        try {
            Optional<Method> writeMethod = ReflectionUtils.getWriteMethod(origin.getField());
            if (writeMethod.isPresent()) {
                writeMethod.get().invoke(origin.getObject(), value);
            } else {
                origin.getField().set(origin.getObject(), value);
            }
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to set entry value", e);
        }
    }

    @Override
    public TranslationKey getTranslation() {
        if (translation == null) {
            Optional<ConfigEntry> annotation = origin.getOptionalAnnotation(ConfigEntry.class);
            if (annotation.isPresent() && !StringUtils.isBlank(annotation.get().translationKey())) {
                translation = origin.getParent().getTranslation().append(annotation.get().translationKey());
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
                    if (StringUtils.isBlank(key)) {
                        throw new IllegalAnnotationParameterException("Tooltip translation key of entry " + origin.getField() + " may not be blank");
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
            setValue((T) node.get(type));
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

}