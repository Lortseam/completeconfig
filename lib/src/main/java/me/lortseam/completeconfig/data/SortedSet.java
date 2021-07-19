package me.lortseam.completeconfig.data;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.lortseam.completeconfig.data.structure.StructurePart;
import me.lortseam.completeconfig.data.structure.Identifiable;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class SortedSet<T extends StructurePart & Identifiable> extends AbstractSet<T> {

    protected final Parent parent;
    private final Map<String, T> map = new LinkedHashMap<>();

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
