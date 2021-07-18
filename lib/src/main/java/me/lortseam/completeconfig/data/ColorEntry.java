package me.lortseam.completeconfig.data;

import lombok.Getter;

public class ColorEntry<T> extends Entry<T> {

    @Getter
    private final boolean alphaMode;

    public ColorEntry(EntryOrigin origin, boolean alphaMode) {
        super(origin);
        this.alphaMode = alphaMode;
    }

}
