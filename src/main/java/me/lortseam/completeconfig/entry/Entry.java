package me.lortseam.completeconfig.entry;

import lombok.*;
import lombok.experimental.Accessors;
import me.lortseam.completeconfig.api.ConfigEntryContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

public class Entry<T> {

    private static final Logger LOGGER = LogManager.getLogger();

    @Getter
    private final Field field;
    @Getter
    private final Class<T> type;
    @Getter
    private final ConfigEntryContainer parentObject;
    @Getter
    private final T defaultValue;
    @Getter
    private final String customTranslationKey;
    @Getter
    private final String[] customTooltipKeys;
    @Getter
    private final Extras<T> extras;
    private final List<Listener> listeners = new ArrayList<>();
    private final boolean forceUpdate;

    private Entry(Field field, Class<T> type, ConfigEntryContainer parentObject, String customTranslationKey, String[] customTooltipKeys, Extras<T> extras, boolean forceUpdate) {
        this.field = field;
        this.type = type;
        this.parentObject = parentObject;
        this.customTranslationKey = customTranslationKey;
        this.customTooltipKeys = customTooltipKeys;
        this.extras = extras;
        this.forceUpdate = forceUpdate;
        defaultValue = getValue();
    }

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
        if (method.getParameterCount() != 1 || method.getParameterTypes()[0] != type) {
            throw new IllegalArgumentException("Listener method " + method + " has wrong parameter type(s)");
        }
        if (!method.isAccessible()) {
            method.setAccessible(true);
        }
        listeners.add(new Listener(method, parentObject));
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Accessors(chain = true)
    public static class Builder {

        public static Builder create(Field field, ConfigEntryContainer parentObject) {
            return new Builder(field, parentObject);
        }

        private final Field field;
        private final ConfigEntryContainer parentObject;
        @Setter
        private String customTranslationKey;
        @Setter
        private String[] customTooltipKeys;
        @Setter
        private boolean forceUpdate;
        private Bounds bounds;

        public <N extends Number> Builder setBounds(N min, N max) {
            bounds = new Bounds<>(min, max);
            return this;
        }

        public Entry<?> build() {
            return new Entry<>(field, field.getType(), parentObject, customTranslationKey, customTooltipKeys, new Extras<>(bounds), forceUpdate);
        }

    }

    @AllArgsConstructor
    private static class Listener {

        private final Method method;
        private final ConfigEntryContainer parentObject;

    }

}