package me.lortseam.completeconfig.data;

import lombok.Getter;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigEntryContainer;
import me.lortseam.completeconfig.data.gui.TranslationIdentifier;
import me.lortseam.completeconfig.data.part.DataPart;
import me.lortseam.completeconfig.exception.IllegalAnnotationParameterException;
import me.lortseam.completeconfig.exception.IllegalAnnotationTargetException;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;

public class Entry<T> extends EntryBase<T> implements DataPart {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<Field, EntryBase> entries = new HashMap<>();

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
    private TranslationIdentifier[] customTooltipTranslations;
    private boolean forceUpdate;
    private boolean requiresRestart;
    @Getter
    private final Extras<T> extras = new Extras<>(this);
    private String comment;
    private final List<Listener<T>> listeners = new ArrayList<>();

    private Entry(Field field, ConfigEntryContainer parentObject, TranslationIdentifier parentTranslation) {
        super(field);
        this.parentObject = parentObject;
        defaultValue = getValue();
        this.parentTranslation = parentTranslation;
    }

    public T getValue() {
        if (update()) {
            return getValue();
        }
        return getFieldValue();
    }

    private T getFieldValue() {
        try {
            return (T) Objects.requireNonNull(field.get(parentObject));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void setValue(T value) {
        update(value);
    }

    private boolean update() {
        return update(getFieldValue());
    }

    private boolean update(T value) {
        if (extras.getBounds() != null) {
            if (new BigDecimal(value.toString()).compareTo(new BigDecimal(extras.getBounds().getMin().toString())) < 0) {
                LOGGER.warn("[CompleteConfig] Tried to set value of field " + field + " to a value less than minimum bound, setting to minimum now!");
                value = extras.getBounds().getMin();
            } else if (new BigDecimal(value.toString()).compareTo(new BigDecimal(extras.getBounds().getMax().toString())) > 0) {
                LOGGER.warn("[CompleteConfig] Tried to set value of field " + field + " to a value greater than maximum bound, setting to maximum now!");
                value = extras.getBounds().getMax();
            }
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
        TranslationIdentifier[] translations = null;
        if (customTooltipTranslations != null) {
            translations = customTooltipTranslations;
        } else {
            TranslationIdentifier defaultTooltipTranslation = getTranslation().append("tooltip");
            if (defaultTooltipTranslation.exists()) {
                translations = new TranslationIdentifier[] {defaultTooltipTranslation};
            } else {
                List<TranslationIdentifier> defaultTooltipTranslations = new ArrayList<>();
                for(int i = 0;; i++) {
                    TranslationIdentifier key = defaultTooltipTranslation.append(Integer.toString(i));
                    if(key.exists()) {
                        defaultTooltipTranslations.add(key);
                    } else {
                        if (!defaultTooltipTranslations.isEmpty()) {
                            translations = defaultTooltipTranslations.toArray(new TranslationIdentifier[0]);
                        }
                        break;
                    }
                }
            }
        }
        return translations != null ? Optional.of(Arrays.stream(translations).map(TranslationIdentifier::translate).toArray(Text[]::new)) : Optional.empty();
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
            String customTranslationKey = annotation.customTranslationKey();
            if (!StringUtils.isBlank(customTranslationKey)) {
                customTranslation = parentTranslation.getModTranslation().appendKey(customTranslationKey);
            }
            String[] customTooltipKeys = annotation.customTooltipKeys();
            if (customTooltipKeys.length > 0) {
                if (Arrays.stream(customTooltipKeys).anyMatch(StringUtils::isBlank)) {
                    throw new IllegalAnnotationParameterException("Entry tooltip key(s) must not be blank");
                }
                customTooltipTranslations = Arrays.stream(customTooltipKeys).map(key -> parentTranslation.getModTranslation().appendKey(key)).toArray(TranslationIdentifier[]::new);
            }
            forceUpdate = annotation.forceUpdate();
            requiresRestart = annotation.requiresRestart();
            String comment = annotation.comment();
            if (!StringUtils.isBlank(comment)) {
                this.comment = comment;
            }
        }
        if (field.isAnnotationPresent(ConfigEntry.Bounded.Integer.class)) {
            if (field.getType() != int.class && field.getType() != Integer.class) {
                throw new IllegalAnnotationTargetException("Cannot apply Integer bound to non Integer field " + field);
            }
            ConfigEntry.Bounded.Integer bounds = field.getDeclaredAnnotation(ConfigEntry.Bounded.Integer.class);
            extras.setBounds(bounds.min(), bounds.max(), bounds.slider());
        }
        if (field.isAnnotationPresent(ConfigEntry.Bounded.Long.class)) {
            if (field.getType() != long.class && field.getType() != Long.class) {
                throw new IllegalAnnotationTargetException("Cannot apply Long bound to non Long field " + field);
            }
            ConfigEntry.Bounded.Long bounds = field.getDeclaredAnnotation(ConfigEntry.Bounded.Long.class);
            extras.setBounds(bounds.min(), bounds.max(), bounds.slider());
        }
        if (field.isAnnotationPresent(ConfigEntry.Bounded.Float.class)) {
            if (field.getType() != float.class && field.getType() != Float.class) {
                throw new IllegalAnnotationTargetException("Cannot apply Float bound to non Float field " + field);
            }
            ConfigEntry.Bounded.Float bounds = field.getDeclaredAnnotation(ConfigEntry.Bounded.Float.class);
            extras.setBounds(bounds.min(), bounds.max(), false);
        }
        if (field.isAnnotationPresent(ConfigEntry.Bounded.Double.class)) {
            if (field.getType() != double.class && field.getType() != Double.class) {
                throw new IllegalAnnotationTargetException("Cannot apply Double bound to non Double field " + field);
            }
            ConfigEntry.Bounded.Double bounds = field.getDeclaredAnnotation(ConfigEntry.Bounded.Double.class);
            extras.setBounds(bounds.min(), bounds.max(), false);
        }
        if (Enum.class.isAssignableFrom(field.getType())) {
            if (field.isAnnotationPresent(ConfigEntry.EnumOptions.class)) {
                extras.setEnumOptions(field.getDeclaredAnnotation(ConfigEntry.EnumOptions.class).displayType());
            } else {
                extras.setEnumOptions(EnumOptions.DisplayType.DEFAULT);
            }
        } else if (field.isAnnotationPresent(ConfigEntry.EnumOptions.class)) {
            throw new IllegalAnnotationTargetException("Cannot apply enum options to non enum field " + field);
        }
    }

    @Override
    public void apply(CommentedConfigurationNode node) {
        try {
            setValue((T) node.get(type));
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
            Entry<T> entry = new Entry<>(field, parentObject, parentTranslation);
            for (Consumer<Entry<T>> interaction : interactions) {
                interaction.accept(entry);
            }
            entries.put(field, entry);
            return entry;
        }

    }

}