package me.lortseam.completeconfig.data;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import me.lortseam.completeconfig.api.ConfigEntries;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.data.structure.Identifiable;
import me.lortseam.completeconfig.data.structure.StructurePart;
import me.lortseam.completeconfig.data.structure.client.DescriptionSupplier;
import me.lortseam.completeconfig.data.structure.client.Translatable;
import me.lortseam.completeconfig.data.transform.Transformation;
import me.lortseam.completeconfig.text.TranslationBase;
import me.lortseam.completeconfig.text.TranslationKey;
import me.lortseam.completeconfig.util.ReflectionUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

@Slf4j(topic = "CompleteConfig")
public class Entry<T> implements StructurePart, Identifiable, Translatable, DescriptionSupplier {

    private static final Transformation DEFAULT_TRANSFORMATION = new Transformation(Transformation.filter(), Entry::new);

    static Entry<?> create(EntryOrigin origin) {
        return Stream.concat(origin.getRoot().getRegistry().getTransformations().stream(), Stream.of(DEFAULT_TRANSFORMATION)).filter(transformation -> {
            return transformation.test(origin);
        }).findFirst().orElseThrow(() -> {
            return new UnsupportedOperationException("No suitable transformation found for field " + origin.getField());
        }).getTransformer().transform(origin);
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
    private TranslationKey descriptionTranslation;
    @Accessors(fluent = true)
    @Getter
    private final boolean requiresRestart;
    private final String comment;
    private final Setter<T> setter;
    private final UnaryOperator<T> revisor;

    protected Entry(EntryOrigin origin, UnaryOperator<T> revisor) {
        ConfigRegistry.registerEntryOrigin(origin);
        this.origin = origin;
        this.revisor = revisor;
        if (!getField().canAccess(origin.getObject())) {
            getField().setAccessible(true);
        }
        typeClass = (Class<T>) ReflectionUtils.getTypeClass(origin.getType());
        Optional<ConfigEntry> annotation = origin.getMainAnnotation();
        id = annotation.isPresent() && !annotation.get().value().isBlank() ? annotation.get().value() : getField().getName();
        requiresRestart = annotation.isPresent() && annotation.get().requiresRestart();
        comment = annotation.isPresent() && !annotation.get().comment().isBlank() ? annotation.get().comment() : null;
        setter = ReflectionUtils.getSetterMethod(getField(), origin.getObject()).<Setter<T>>map(method -> method::invoke).orElse((object, value) -> getField().set(object, value));
        defaultValue = getValue();
    }

    protected Entry(EntryOrigin origin) {
        this(origin, null);
    }
    
    private Field getField() {
        return origin.getField();
    }

    public final Type getType() {
        return origin.getType();
    }

    public final Type[] getGenericTypes() {
        return origin.getGenericTypes();
    }

    public final T getValue() {
        if (update()) {
            return getValue();
        }
        return getFieldValue();
    }

    private T getFieldValue() {
        try {
            return (T) Objects.requireNonNull(getField().get(origin.getObject()), getField().toString());
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to get entry value", e);
        }
    }

    public final void setValue(@NonNull T value) {
        update(value);
    }

    private boolean update() {
        return update(getFieldValue());
    }

    private boolean update(T value) {
        if (revisor != null) {
            value = revisor.apply(value);
        }
        if (value.equals(getFieldValue())) {
            return false;
        }
        set(value);
        origin.getContainer().onContainerEntryUpdate();
        origin.getRoot().onConfigEntryUpdate();
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
    public final TranslationKey getNameTranslation() {
        if (translation == null) {
            Optional<ConfigEntry> annotation = origin.getMainAnnotation();
            if (annotation.isPresent() && !annotation.get().nameKey().isBlank()) {
                translation = origin.getRoot().getBaseTranslation().append(annotation.get().nameKey());
            } else {
                var translationBase = origin.getClassAnnotation().map(ConfigEntries::translationBase).orElse(TranslationBase.INSTANCE);
                translation = origin.getParent().getBaseTranslation(translationBase, origin.getDeclaringClass()).append(id);
            }
        }
        return translation;
    }

    @Override
    public final Optional<TranslationKey> getDescriptionTranslation() {
        if (descriptionTranslation == null) {
            Optional<ConfigEntry> annotation = origin.getMainAnnotation();
            if (annotation.isPresent() && !annotation.get().descriptionKey().isBlank()) {
                descriptionTranslation = origin.getRoot().getBaseTranslation().append(annotation.get().descriptionKey());
            } else {
                descriptionTranslation = getNameTranslation().append("description");
            }
        }
        return descriptionTranslation.exists() ? Optional.of(descriptionTranslation) : Optional.empty();
    }

    @Environment(EnvType.CLIENT)
    public Optional<Function<T, Text>> getValueFormatter() {
        return Optional.empty();
    }

    @Override
    public final void apply(CommentedConfigurationNode node) {
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
    public final void fetch(CommentedConfigurationNode node) {
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
    public final String toString() {
        return getField().toString();
    }

    @FunctionalInterface
    private interface Setter<T> {

        void set(Object object, T value) throws IllegalAccessException, InvocationTargetException;

    }

}