package me.lortseam.completeconfig;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class ConfigMap<T> extends LinkedHashMap<String, T> {

    @Override
    public T put(String id, T value) {
        check(id, value);
        return super.put(id, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends T> m) {
        m.forEach(this::check);
        super.putAll(m);
    }

    private void check(String id, T value) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("ID of type " + value.getClass().getSimpleName() + " must not be null or blank");
        }
        if (containsKey(id)) {
            throw new UnsupportedOperationException("A value of type " + value.getClass().getSimpleName() + " with ID " + id + " already exists in the corresponding structure");
        }
    }

}
