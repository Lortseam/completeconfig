package me.lortseam.completeconfig.entry;

import lombok.Getter;
import me.lortseam.completeconfig.api.ConfigEntryContainer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Entry<T> {

    private final Field field;
    @Getter
    private final Class<T> type;
    @Getter
    private final ConfigEntryContainer parentObject;
    @Getter
    private final T defaultValue;
    @Getter
    private final String translationKey;
    private final Map<Method, ConfigEntryContainer> saveConsumers = new HashMap<>();

    public Entry(Field field, Class<T> type, ConfigEntryContainer parentObject, String translationKey) {
        this.field = field;
        this.type = type;
        this.parentObject = parentObject;
        defaultValue = getValue();
        this.translationKey = translationKey;
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

}