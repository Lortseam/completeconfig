package me.lortseam.completeconfig.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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

public class Entry<T> implements EntryAccessor<T>, DataPart {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<Field, EntryAccessor> ENTRIES = new HashMap<>();

    static EntryAccessor<?> of(String fieldName, Class<? extends ConfigEntryContainer> parentClass) {
        try {
            return of(parentClass.getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    static EntryAccessor<?> of(Field field) {
        return ENTRIES.computeIfAbsent(field, absentField -> new Draft<>(field));
    }

    @Getter
    private final Field field;
    @Getter
    private final Class<T> type;
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
    private final List<Listener<T>> listeners = new ArrayList<>();

    private Entry(Field field, Class<T> type, ConfigEntryContainer parentObject, TranslationIdentifier parentTranslation) {
        this.field = field;
        this.type = type;
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

    @Override
    public void connect(Consumer<Entry<T>> modifier) {
        modifier.accept(this);
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
            setValue(node.get(type));
        } catch (SerializationException e) {
            //TODO
            e.printStackTrace();
        }
    }

    @Override
    public void fetch(CommentedConfigurationNode node) {
        try {
            node.set(type, getValue());
        } catch (SerializationException e) {
            //TODO
            e.printStackTrace();
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    static class Draft<T> implements EntryAccessor<T> {

        static <T> Draft<T> of(Field field) {
            EntryAccessor<T> accessor = (EntryAccessor<T>) Entry.of(field);
            if (!(accessor instanceof Draft)) {
                throw new UnsupportedOperationException("Entry draft of " + field + " was already built");
            }
            return (Draft<T>) accessor;
        }

        private final Field field;
        private final List<Consumer<Entry<T>>> modifiers = new ArrayList<>();

        @Override
        public Class<T> getType() {
            return (Class<T>) field.getType();
        }

        @Override
        public void connect(Consumer<Entry<T>> modifier) {
            modifiers.add(modifier);
        }

        Entry<T> build(ConfigEntryContainer parentObject, TranslationIdentifier parentTranslation) {
            Entry<T> entry = new Entry<>(field, getType(), parentObject, parentTranslation);
            for (Consumer<Entry<T>> modifier : modifiers) {
                modifier.accept(entry);
            }
            ENTRIES.put(field, entry);
            return entry;
        }

    }

}