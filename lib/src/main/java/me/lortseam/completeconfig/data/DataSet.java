package me.lortseam.completeconfig.data;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.lortseam.completeconfig.data.structure.DataPart;
import me.lortseam.completeconfig.data.structure.Identifiable;
import me.lortseam.completeconfig.data.text.TranslationKey;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class DataSet<T extends DataPart & Identifiable> extends AbstractSet<T> {

    private final Map<String, T> map = new LinkedHashMap<>();
    protected final TranslationKey translation;

    @Override
    public @NotNull Iterator<T> iterator() {
        return map.values().iterator();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean add(T t) {
        return map.put(t.getId(), t) != t;
    }

}
