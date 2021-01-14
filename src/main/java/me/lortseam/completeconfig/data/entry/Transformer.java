package me.lortseam.completeconfig.data.entry;

import me.lortseam.completeconfig.data.Entry;

@FunctionalInterface
public interface Transformer<O extends EntryOrigin> {

    Entry<?> transform(O origin);

}
