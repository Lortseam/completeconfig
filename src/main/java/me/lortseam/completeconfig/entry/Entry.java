package me.lortseam.completeconfig.entry;

import lombok.*;
import lombok.experimental.Accessors;
import me.lortseam.completeconfig.api.ConfigEntryContainer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Entry<T> {

    @Getter(AccessLevel.PACKAGE)
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
    private final Extras<T> extras;
    private final Map<Method, ConfigEntryContainer> saveConsumers = new HashMap<>();

    private Entry(Field field, Class<T> type, ConfigEntryContainer parentObject, String customTranslationKey, Extras<T> extras) {
        this.field = field;
        this.type = type;
        this.parentObject = parentObject;
        defaultValue = getValue();
        this.customTranslationKey = customTranslationKey;
        this.extras = extras;
    }

    public T getValue() {
        try {
            return (T) field.get(parentObject);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void setValue(T value) {
        if (saveConsumers.values().stream().noneMatch(parentObject -> parentObject == this.parentObject)) {
            try {
                field.set(parentObject, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        if (!saveConsumers.isEmpty()) {
            saveConsumers.forEach((method, parentObject) -> {
                try {
                    method.invoke(parentObject, value);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public void addSaveConsumer(Method method, ConfigEntryContainer parentObject) {
        if (method.getParameterCount() != 1 || method.getParameterTypes()[0] != type) {
            throw new IllegalArgumentException("Save consumer method " + method + " has wrong parameter type(s)!");
        }
        if (!method.isAccessible()) {
            method.setAccessible(true);
        }
        saveConsumers.put(method, parentObject);
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    public static class Extras<T> {

        @Getter
        private final Bounds<T> bounds;

    }

    //TODO: Bounds auch beim Einlesen aus JSON beachten
    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    public static class Bounds<T> {

        @Getter
        private final T min;
        @Getter
        private final T max;

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
        private Bounds bounds;

        public <N extends Number> Builder setBounds(N min, N max) {
            bounds = new Bounds<>(min, max);
            return this;
        }

        public Entry<?> build() {
            return new Entry<>(field, field.getType(), parentObject, customTranslationKey, new Extras<>(bounds));
        }

    }

}