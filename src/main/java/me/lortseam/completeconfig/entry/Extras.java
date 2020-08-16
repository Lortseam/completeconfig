package me.lortseam.completeconfig.entry;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class Extras<T> {

    private final Entry<T> entry;
    @Getter
    private Bounds<T> bounds;

    public <N extends Number> void setBounds(N min, N max) {
        //TODO: Check for correct min/max type
        bounds = new Bounds<>((T) min, (T) max);
    }

}