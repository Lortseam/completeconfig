package me.lortseam.completeconfig.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.lortseam.completeconfig.api.ConfigContainer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class Listener<T> {

    private final Method method;
    @Getter(AccessLevel.PACKAGE)
    private final ConfigContainer parentObject;

    void invoke(T value) {
        try {
            method.invoke(parentObject, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
