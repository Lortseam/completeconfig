package me.lortseam.completeconfig.data;

import com.google.common.base.Predicates;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import me.lortseam.completeconfig.CompleteConfig;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.data.entry.EntryOrigin;
import me.lortseam.completeconfig.data.entry.Transformation;
import me.lortseam.completeconfig.data.structure.DataPart;
import me.lortseam.completeconfig.data.text.TranslationIdentifier;
import me.lortseam.completeconfig.exception.IllegalAnnotationParameterException;
import me.lortseam.completeconfig.extensions.ConfigExtensionPattern;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

@Log4j2
public class Entry<T> extends EntryBase<T> implements DataPart {

    private static final List<Transformation> transformations = Lists.newArrayList(
            Transformation.byAnnotation(ConfigEntry.Boolean.class).andType(boolean.class, Boolean.class).transforms(BooleanEntry::new),
            Transformation.byType(boolean.class, Boolean.class).transforms(BooleanEntry::new),
            Transformation.byAnnotation(ConfigEntry.BoundedInteger.class).andType(int.class, Integer.class).transforms(origin -> {
                ConfigEntry.BoundedInteger bounds = origin.getAnnotation();
                // TODO: Create subtransformations for this case
                return origin.getAnnotation(ConfigEntry.Slider.class).map(slider -> {
                    return (Entry<Integer>) new SliderEntry<>(origin, bounds.min(), bounds.max(), slider);
                }).orElse(new BoundedEntry<>(origin, bounds.min(), bounds.max()));
            }),
            Transformation.byAnnotation(ConfigEntry.BoundedLong.class).andType(long.class, Long.class).transforms(origin -> {
                ConfigEntry.BoundedLong bounds = origin.getAnnotation();
                return origin.getAnnotation(ConfigEntry.Slider.class).map(slider -> {
                    return (Entry<Long>) new SliderEntry<>(origin, bounds.min(), bounds.max(), slider);
                }).orElse(new BoundedEntry<>(origin, bounds.min(), bounds.max()));
            }),
            Transformation.byAnnotation(ConfigEntry.BoundedFloat.class).andType(float.class, Float.class).transforms(origin -> {
                ConfigEntry.BoundedFloat bounds = origin.getAnnotation();
                return new BoundedEntry<>(origin, bounds.min(), bounds.max());
            }),
            Transformation.byAnnotation(ConfigEntry.BoundedDouble.class).andType(double.class, Double.class).transforms(origin -> {
                ConfigEntry.BoundedDouble bounds = origin.getAnnotation();
                return new BoundedEntry<>(origin, bounds.min(), bounds.max());
            }),
            Transformation.by(base -> Enum.class.isAssignableFrom(base.typeClass)).transforms(EnumEntry::new),
            Transformation.byAnnotation(ConfigEntry.Enum.class).and(base -> Enum.class.isAssignableFrom(base.typeClass)).transforms(origin -> new EnumEntry<>(origin, origin.getAnnotation().displayType())),
            Transformation.byAnnotation(ConfigEntry.Color.class).transforms(ColorEntry::new),
            Transformation.byType(TextColor.class).transforms(origin -> new ColorEntry<>(origin, false))
    );
    private static final BiMap<Key, EntryBase> entries = HashBiMap.create();

    static {
        CompleteConfig.getExtensions().stream().map(ConfigExtensionPattern::getTransformations).filter(Objects::nonNull).forEach(extensionTransformations -> {
            transformations.addAll(0, extensionTransformations);
        });
    }

    static EntryBase<?> of(Field field, Class<? extends ConfigContainer> parentClass) {
        return entries.computeIfAbsent(new Key(field, parentClass), absentField -> new Draft<>(field));
    }

    private final ConfigContainer parentObject;
    private String customID;
    @Getter
    private final T defaultValue;
    private final TranslationIdentifier parentTranslation;
    private TranslationIdentifier customTranslation;
    private TranslationIdentifier[] customTooltipTranslation;
    private boolean forceUpdate;
    private boolean requiresRestart;
    private String comment;
    private final UnaryOperator<T> modifier;
    private final List<Listener<T>> listeners = new ArrayList<>();

    protected Entry(EntryOrigin origin, UnaryOperator<T> modifier) {
        super(origin.getField());
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        parentObject = origin.getParentObject();
        parentTranslation = origin.getParentTranslation();
        this.modifier = modifier;
        defaultValue = getValue();
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
            throw new RuntimeException(e);
        }
    }

    public void setValue(@NonNull T value) {
        update(value);
    }

    private boolean update() {
        return update(getFieldValue());
    }

    private boolean update(T value) {
        if (modifier != null) {
            value = modifier.apply(value);
        }
        if (value.equals(getFieldValue())) {
            return false;
        }
        set(value);
        return true;
    }

    private void set(T value) {
        if (listeners.stream().noneMatch(listener -> listener.getParentObject() == parentObject) || forceUpdate) {
            try {
                field.set(parentObject, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        for (Listener<T> listener : listeners) {
            listener.invoke(value);
        }
    }

    void addListener(Method method, ConfigContainer parentObject) {
        listeners.add(new Listener<>(method, parentObject));
    }

    @Override
    public String getID() {
        return customID != null ? customID : field.getName();
    }

    TranslationIdentifier getTranslation() {
        return customTranslation != null ? customTranslation : parentTranslation.append(getID());
    }

    public Text getText() {
        return getTranslation().toText();
    }

    public Optional<Text[]> getTooltip() {
        return (customTooltipTranslation != null ? Optional.of(customTooltipTranslation) : getTranslation().appendTooltip()).map(lines -> {
            return Arrays.stream(lines).map(TranslationIdentifier::toText).toArray(Text[]::new);
        });
    }

    public boolean requiresRestart() {
        return requiresRestart;
    }

    void resolve(Field field) {
        if (field.isAnnotationPresent(ConfigEntry.class)) {
            ConfigEntry annotation = field.getDeclaredAnnotation(ConfigEntry.class);
            String id = annotation.value();
            if (!StringUtils.isBlank(id)) {
                customID = id;
            }
            String customTranslationKey = annotation.translationKey();
            if (!StringUtils.isBlank(customTranslationKey)) {
                customTranslation = parentTranslation.root().append(customTranslationKey);
            }
            String[] customTooltipTranslationKeys = annotation.tooltipTranslationKeys();
            if (customTooltipTranslationKeys.length > 0) {
                if (Arrays.stream(customTooltipTranslationKeys).anyMatch(StringUtils::isBlank)) {
                    throw new IllegalAnnotationParameterException("Entry tooltip translation key(s) must not be blank");
                }
                customTooltipTranslation = Arrays.stream(customTooltipTranslationKeys).map(key -> parentTranslation.root().append(key)).toArray(TranslationIdentifier[]::new);
            }
            forceUpdate = annotation.forceUpdate();
            requiresRestart = annotation.requiresRestart();
            String comment = annotation.comment();
            if (!StringUtils.isBlank(comment)) {
                this.comment = comment;
            }
        }
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
            logger.error("[CompleteConfig] Failed to apply value to entry!", e);
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
            logger.error("[CompleteConfig] Failed to fetch value from entry!", e);
        }
    }

    @Override
    void interact(Consumer<Entry<T>> interaction) {
        interaction.accept(this);
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    private static class Key {

        private final Field field;
        private final Class<? extends ConfigContainer> parentClass;

    }

    static class Draft<T> extends EntryBase<T> {

        static <T> Draft<T> of(Field field, Class<? extends ConfigContainer> parentClass) {
            EntryBase<T> accessor = (EntryBase<T>) Entry.of(field, parentClass);
            if (!(accessor instanceof Draft)) {
                throw new UnsupportedOperationException("Entry draft of field " + field + " was already built");
            }
            return (Draft<T>) accessor;
        }

        private final List<Consumer<Entry<T>>> interactions = new ArrayList<>();

        private Draft(Field field) {
            super(field);
        }

        @Override
        void interact(Consumer<Entry<T>> interaction) {
            interactions.add(interaction);
        }

        Entry<T> build(ConfigContainer parentObject, TranslationIdentifier parentTranslation) {
                                                                                                                            // TODO: Use a default transformer instead of transformation
            Entry<T> entry = transformations.stream().filter(transformation -> transformation.test(this)).findFirst().orElse(Transformation.by(Predicates.alwaysTrue()).transforms(Entry::new)).transform(this, parentObject, parentTranslation);
            for (Consumer<Entry<T>> interaction : interactions) {
                interaction.accept(entry);
            }
            entries.put(entries.inverse().get(this), entry);
            return entry;
        }

    }

}