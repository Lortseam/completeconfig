package me.lortseam.completeconfig.collection;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.lortseam.completeconfig.entry.Entry;

import java.util.LinkedHashMap;

@NoArgsConstructor
public class Collection {

    //TODO: Entries und Collections werden auch in der Config Json Datei gespeichtert, wenn sie leer sind
    @Getter
    private final LinkedHashMap<String, Entry> entries = new LinkedHashMap<>();
    @Getter
    private final LinkedHashMap<String, Collection> collections = new LinkedHashMap<>();

}