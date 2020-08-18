package me.lortseam.completeconfig.entry;

import com.google.common.collect.MoreCollectors;
import lombok.*;
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
    private static final Set<Entry> ENTRIES = new HashSet<>();

    public static Entry<?> of(String fieldName, Class<? extends ConfigEntryContainer> parentClass) {
        try {
            Field field = parentClass.getDeclaredField(fieldName);
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            return of(field);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> Entry<T> of(Field field) {
        return ENTRIES.stream().filter(entry -> entry.field.equals(field)).collect(MoreCollectors.toOptional()).orElseGet(() -> {
            Entry<T> entry = new Entry<>(field, (Class<T>) field.getType());
            ENTRIES.add(entry);
            return entry;
        });
    }

    public static <T> Entry<T> of(Field field, ConfigEntryContainer parentObject) {
        Entry<T> entry = of(field);
        entry.parentObject = parentObject;
        entry.defaultValue = entry.getValue();
        return entry;
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
    @Setter
    private String customTranslationKey;
    @Getter
    @Setter
    private String[] customTooltipKeys;
    @Getter
    private Extras<T> extras = new Extras<>(this);
    private final List<Listener> listeners = new ArrayList<>();
    @Setter
    private boolean forceUpdate;

    public T getValue() {
        if (updateValueIfNecessary()) {
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
        updateValueIfNecessary(value);
    }

    private boolean updateValueIfNecessary() {
        return updateValueIfNecessary(getFieldValue());
    }

    private boolean updateValueIfNecessary(T value) {
        if (extras.getBounds() != null) {
            if (new BigDecimal(value.toString()).compareTo(new BigDecimal(extras.getBounds().getMin().toString())) < 0) {
                LOGGER.warn("[CompleteConfig] Tried to set value of field " + field + " to a value less than minimum bound, setting to minimum now!");
                value = (T) extras.getBounds().getMin();
            } else if (new BigDecimal(value.toString()).compareTo(new BigDecimal(extras.getBounds().getMax().toString())) > 0) {
                LOGGER.warn("[CompleteConfig] Tried to set value of field " + field + " to a value greater than maximum bound, setting to maximum now!");
                value = (T) extras.getBounds().getMax();
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

    public void addListener(Method method, ConfigEntryContainer parentObject) {
        listeners.add(new Listener(method, parentObject));
    }

    @AllArgsConstructor
    private static class Listener {

        private final Method method;
        private final ConfigEntryContainer parentObject;

    }

}