package me.lortseam.completeconfig.data;

import me.lortseam.completeconfig.data.entry.EntryOrigin;

public class SliderEntry<T extends Number> extends BoundedEntry<T> {

    public SliderEntry(EntryOrigin origin, T min, T max) {
        super(origin, min, max);
    }

}
