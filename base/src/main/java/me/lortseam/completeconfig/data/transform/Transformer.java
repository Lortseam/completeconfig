package me.lortseam.completeconfig.data.transform;

import me.lortseam.completeconfig.data.Entry;
import me.lortseam.completeconfig.data.EntryOrigin;

@FunctionalInterface
public interface Transformer {

    Entry<?> transform(EntryOrigin origin);

}
