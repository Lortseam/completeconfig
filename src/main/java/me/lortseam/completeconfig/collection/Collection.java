package me.lortseam.completeconfig.collection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.lortseam.completeconfig.entry.Entry;

import java.util.LinkedHashMap;

@AllArgsConstructor
public class Collection {

    @Getter
    private final LinkedHashMap<String, Entry> entries;
    @Getter
    private final LinkedHashMap<String, Collection> collections;

    public Collection() {
        this(new LinkedHashMap<>(), new LinkedHashMap<>());
    }

}