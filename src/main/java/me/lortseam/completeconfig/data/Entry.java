package me.lortseam.completeconfig.data;

import com.google.common.collect.MoreCollectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.lortseam.completeconfig.api.ConfigEntryContainer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

public class Entry<T> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<Entry> ENTRIES = new HashSet<>();

    static Entry<?> of(Collection parent, String fieldName, Class<? extends ConfigEntryContainer> parentClass) {
        try {
            Field field = parentClass.getDeclaredField(fieldName);
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            return of(parent, field);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> Entry<T> of(Collection parent, Field field) {
        return ENTRIES.stream().filter(entry -> entry.field.equals(field)).collect(MoreCollectors.toOptional()).orElseGet(() -> {
            Entry<T> entry = new Entry<>(parent, field, (Class<T>) field.getType());
            ENTRIES.add(entry);
            return entry;
        });
    }

    static <T> Entry<T> of(Collection parent, Field field, ConfigEntryContainer parentObject) {
        Entry<T> entry = of(parent, field);
        entry.parentObject = parentObject;
        entry.defaultValue = entry.getValue();
        return entry;
    }

    @Getter(AccessLevel.PACKAGE)
    private final Collection parent;
    @Getter
    private final Field field;
    @Setter(AccessLevel.PACKAGE)
    private String customID;
    @Getter
    private final Class<T> type;
    private ConfigEntryContainer parentObject;
    @Getter
    private T defaultValue;
    @Setter(AccessLevel.PACKAGE)
    private String customTranslationKey;
    private String[] tooltipTranslationKeys;
    @Getter
    private final Extras<T> extras = new Extras<>(this);
    private final List<Listener> listeners = new ArrayList<>();
    @Setter(AccessLevel.PACKAGE)
    private boolean forceUpdate;
    @Setter(AccessLevel.PACKAGE)
    private boolean requiresRestart;

    private Entry(Collection parent, Field field, Class<T> type) {
        this.parent = parent;
        this.field = field;
        this.type = type;
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
        if (listeners.stream().noneMatch(listener -> listener.parentObject == parentObject) || forceUpdate) {
            try {
                field.set(parentObject, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        for (Listener listener : listeners) {
            try {
                listener.method.invoke(listener.parentObject, value);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    void addListener(Method method, ConfigEntryContainer parentObject) {
        listeners.add(new Listener(method, parentObject));
    }

    String getID() {
        return customID != null ? customID : field.getName();
    }

    String getTranslationKey() {
        return customTranslationKey != null ? customTranslationKey : parent.getTranslationKey() + "." + getID();
    }

    public Text getText() {
        return new TranslatableText(getTranslationKey());
    }

    void setCustomTooltipTranslationKeys(String[] tooltipTranslationKeys) {
        this.tooltipTranslationKeys = tooltipTranslationKeys;
    }

    public Optional<Text[]> getTooltip() {
        if (tooltipTranslationKeys == null) {
            String defaultTooltipTranslationKey = getTranslationKey() + ".tooltip";
            if (I18n.hasTranslation(defaultTooltipTranslationKey)) {
                tooltipTranslationKeys = new String[] {defaultTooltipTranslationKey};
            } else {
                List<String> defaultTooltipTranslationKeys = new ArrayList<>();
                for(int i = 0;; i++) {
                    String key = defaultTooltipTranslationKey + "." + i;
                    if(I18n.hasTranslation(key)) {
                        defaultTooltipTranslationKeys.add(key);
                    } else {
                        if (!defaultTooltipTranslationKeys.isEmpty()) {
                            tooltipTranslationKeys = defaultTooltipTranslationKeys.toArray(new String[0]);
                        }
                        break;
                    }
                }
            }
        }
        return tooltipTranslationKeys != null ? Optional.of(Arrays.stream(tooltipTranslationKeys).map(TranslatableText::new).toArray(Text[]::new)) : Optional.empty();
    }

    <N extends Number> void setBounds(N min, N max, boolean slider) {
        extras.setBounds(min, max, slider);
    }

    void setEnumOptions(EnumOptions.DisplayType displayType) {
        extras.setEnumOptions(displayType);
    }

    public boolean requiresRestart() {
        return requiresRestart;
    }

    @AllArgsConstructor
    private static class Listener {

        private final Method method;
        private final ConfigEntryContainer parentObject;

    }

}