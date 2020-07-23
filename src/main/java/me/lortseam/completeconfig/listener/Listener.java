package me.lortseam.completeconfig.listener;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lortseam.completeconfig.api.ConfigEntryContainer;

import java.lang.reflect.Method;

@RequiredArgsConstructor
public class Listener {

    @Getter
    private final Method method;
    @Getter
    private final ConfigEntryContainer parentObject;
    @Getter
    private final String fieldName;
    @Getter
    private final Class<? extends ConfigEntryContainer> fieldClass;

    public Listener(Method method, ConfigEntryContainer parentObject, String fieldName) {
        this.method = method;
        this.parentObject = parentObject;
        this.fieldName = fieldName;
        this.fieldClass = parentObject.getClass();
    }

}
