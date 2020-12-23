package me.lortseam.completeconfig.data;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ConfigMap<T> extends LinkedHashMap<String, T> {

    protected final String modTranslationKey;

    void putUnique(String id, T value) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("ID of type " + value.getClass().getSimpleName() + " must not be null or blank");
        }
        if (containsKey(id)) {
            throw new UnsupportedOperationException("A value of type " + value.getClass().getSimpleName() + " with ID " + id + " already exists in the corresponding structure");
        }
        put(id, value);
    }

}
