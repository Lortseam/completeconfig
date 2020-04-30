package me.lortseam.completeconfig.collection;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.lortseam.completeconfig.entry.Entry;

import java.util.LinkedHashMap;

public class Collection {

    //TODO: Do not save empty maps in json file or completely remove "entries" and "collections" keys in json file
    @Getter
    private final LinkedHashMap<String, Entry> entries = new LinkedHashMap<>();
    @Getter
    private final LinkedHashMap<String, Collection> collections = new LinkedHashMap<>();

}