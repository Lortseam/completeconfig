package me.lortseam.completeconfig.data;

import com.google.common.collect.Lists;
import lombok.Getter;
import me.lortseam.completeconfig.CompleteConfig;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigEntryContainer;
import me.lortseam.completeconfig.data.entry.EntryOrigin;
import me.lortseam.completeconfig.data.entry.Transformation;
import me.lortseam.completeconfig.data.part.DataPart;
import me.lortseam.completeconfig.data.text.TranslationIdentifier;
import me.lortseam.completeconfig.exception.IllegalAnnotationParameterException;
import me.lortseam.completeconfig.extensions.CompleteConfigExtension;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class Entry<T> extends EntryBase<T> implements DataPart {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final List<Transformation> transformations = Lists.newArrayList(
            Transformation.ofAnnotation(ConfigEntry.BoundedInteger.class, origin -> {
                ConfigEntry.BoundedInteger bounds = origin.getAnnotation();
                return new BoundedEntry<>(origin, bounds.min(), bounds.max(), bounds.slider());
            }, int.class, Integer.class),
            Transformation.ofAnnotation(ConfigEntry.BoundedLong.class, origin -> {
                ConfigEntry.BoundedLong bounds = origin.getAnnotation();
                return new BoundedEntry<>(origin, bounds.min(), bounds.max(), bounds.slider());
            }, long.class, Long.class),
            Transformation.ofAnnotation(ConfigEntry.BoundedFloat.class, origin -> {
                ConfigEntry.BoundedFloat bounds = origin.getAnnotation();
                return new BoundedEntry<>(origin, bounds.min(), bounds.max());
            }, float.class, Float.class),
            Transformation.ofAnnotation(ConfigEntry.BoundedDouble.class, origin -> {
                ConfigEntry.BoundedDouble bounds = origin.getAnnotation();
                return new BoundedEntry<>(origin, bounds.min(), bounds.max());
            }, double.class, Double.class),
            Transformation.of(base -> Enum.class.isAssignableFrom(base.typeClass), EnumEntry::new),
            Transformation.ofAnnotation(ConfigEntry.Enum.class, origin -> new EnumEntry<>(origin, origin.getAnnotation().displayType()), base -> Enum.class.isAssignableFrom(base.typeClass)),
            Transformation.ofAnnotation(ConfigEntry.Color.class, origin -> new ColorEntry<>(origin, origin.getAnnotation().alphaMode())),
            Transformation.ofType(TextColor.class, origin -> new ColorEntry<>(origin, false))
    );
    private static final Map<Field, EntryBase> entries = new HashMap<>();

    static {
        CompleteConfig.getExtensions().stream().map(CompleteConfigExtension::getTransformations).filter(Objects::nonNull).forEach(extensionTransformations -> {
            transformations.addAll(0, extensionTransformations);
        });
    }

    static EntryBase<?> of(String fieldName, Class<? extends ConfigEntryContainer> parentClass) {
        try {
            return of(parentClass.getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    static EntryBase<?> of(Field field) {
        return entries.computeIfAbsent(field, absentField -> new Draft<>(field));
    }

    private final ConfigEntryContainer parentObject;
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
            return (T) Objects.requireNonNull(field.get(parentObject), "Entry field value must never be null: " + field);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void setValue(T value) {
        update(Objects.requireNonNull(value, "Entry value must never be null: " + field));
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

    void addListener(Method method, ConfigEntryContainer parentObject) {
        listeners.add(new Listener<>(method, parentObject));
    }

    String getID() {
        return customID != null ? customID : field.getName();
    }

    TranslationIdentifier getTranslation() {
        return customTranslation != null ? customTranslation : parentTranslation.append(getID());
    }

    public Text getText() {
        return getTranslation().translate();
    }

    public Optional<Text[]> getTooltip() {
        TranslationIdentifier[] translation;
        if (customTooltipTranslation != null) {
            translation = customTooltipTranslation;
        } else {
            Optional<TranslationIdentifier[]> defaultTooltip = getTranslation().appendTooltip();
            if (defaultTooltip.isPresent()) {
                translation = defaultTooltip.get();
            } else {
                return Optional.empty();
            }
        }
        return Optional.of(Arrays.stream(translation).map(TranslationIdentifier::translate).toArray(Text[]::new));
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
                customTranslation = parentTranslation.root().appendKey(customTranslationKey);
            }
            String[] customTooltipTranslationKeys = annotation.tooltipTranslationKeys();
            if (customTooltipTranslationKeys.length > 0) {
                if (Arrays.stream(customTooltipTranslationKeys).anyMatch(StringUtils::isBlank)) {
                    throw new IllegalAnnotationParameterException("Entry tooltip translation key(s) must not be blank");
                }
                customTooltipTranslation = Arrays.stream(customTooltipTranslationKeys).map(key -> parentTranslation.root().appendKey(key)).toArray(TranslationIdentifier[]::new);
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
            // value could be null despite the virtual() check (see https://github.com/SpongePowered/Configurate/issues/187)
            if(value == null) return;
            setValue(value);
        } catch (SerializationException e) {
            LOGGER.error("[CompleteConfig] Failed to apply value to entry!", e);
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
            LOGGER.error("[CompleteConfig] Failed to fetch value from entry!", e);
        }
    }

    @Override
    void interact(Consumer<Entry<T>> interaction) {
        interaction.accept(this);
    }

    static class Draft<T> extends EntryBase<T> {

        static <T> Draft<T> of(Field field) {
            EntryBase<T> accessor = (EntryBase<T>) Entry.of(field);
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

        Entry<T> build(ConfigEntryContainer parentObject, TranslationIdentifier parentTranslation) {
            Entry<T> entry = transformations.stream().filter(transformation -> transformation.test(this)).findFirst().orElse(Transformation.of(base -> true, Entry::new)).transform(this, parentObject, parentTranslation);
            for (Consumer<Entry<T>> interaction : interactions) {
                interaction.accept(entry);
            }
            entries.put(field, entry);
            return entry;
        }

    }

}