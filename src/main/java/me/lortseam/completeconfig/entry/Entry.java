package me.lortseam.completeconfig.entry;

import com.google.common.collect.MoreCollectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lortseam.completeconfig.api.ConfigEntryContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Entry<T> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<Entry> entries = new HashSet<>();

    public static Entry<?> of(String fieldName, Class<? extends ConfigEntryContainer> parentClass) {
        try {
            Field field = parentClass.getField(fieldName);
            return entries.stream().filter(entry -> entry.parentObject.getClass() == parentClass && entry.field == field).collect(MoreCollectors.toOptional()).orElse(new Entry<>(field, parentClass));
        } catch (NoSuchFieldException e) {
            //TODO
            throw new RuntimeException(e);
        }
    }

    @Getter
    private final Field field;
    @Getter
    private final Class<T> type;
    @Getter
    private ConfigEntryContainer parentObject;
    @Getter
    private T defaultValue;
    @Getter
    private String customTranslationKey;
    @Getter
    private String[] customTooltipKeys;
    @Getter
    private Extras<T> extras;
    private final List<Listener> listeners = new ArrayList<>();
    private boolean forceUpdate;

    /*private Entry(Field field, Class<T> type, ConfigEntryContainer parentObject, String customTranslationKey, String[] customTooltipKeys, Extras<T> extras, boolean forceUpdate) {
        this.field = field;
        this.type = type;
        this.parentObject = parentObject;
        this.customTranslationKey = customTranslationKey;
        this.customTooltipKeys = customTooltipKeys;
        this.extras = extras;
        this.forceUpdate = forceUpdate;
        defaultValue = getValue();
    }*/

    public T getValue() {
        if (updateValueIfNecessary()) {
            return getValue();
        }
        return get();
    }

    private T get() {
        try {
            return (T) Objects.requireNonNull(field.get(parentObject));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void setValue(T value) {
        updateValueIfNecessary(value);
    }

    private boolean updateValueIfNecessary() {
        return updateValueIfNecessary(get());
    }

    private boolean updateValueIfNecessary(T value) {
        if (extras.getBounds() != null) {
            if (new BigDecimal(value.toString()).compareTo(new BigDecimal(extras.getBounds().getMin().toString())) < 0) {
                LOGGER.warn("[CompleteConfig] Tried to set value of field " + field + " to a value less than minimum bound, setting to minimum now!");
                value = extras.getBounds().getMin();
            } else if (new BigDecimal(value.toString()).compareTo(new BigDecimal(extras.getBounds().getMax().toString())) > 0) {
                LOGGER.warn("[CompleteConfig] Tried to set value of field " + field + " to a value greater than maximum bound, setting to maximum now!");
                value = extras.getBounds().getMax();
            }
        }
        if (value.equals(get())) {
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
        if (!listeners.isEmpty()) {
            for (Listener listener : listeners) {
                try {
                    listener.method.invoke(listener.parentObject, value);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void addListener(Method method, ConfigEntryContainer parentObject) {
        listeners.add(new Listener(method, parentObject));
    }

    @AllArgsConstructor
    private static class Listener {

        private final Method method;
        private final ConfigEntryContainer parentObject;

    }

}