package me.lortseam.completeconfig.data.entry;

import me.lortseam.completeconfig.data.Entry;

@FunctionalInterface
public interface Transformer {

    Entry<?> transform(EntryOrigin origin);

}
