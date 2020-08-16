package me.lortseam.completeconfig.entry;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ClassUtils;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class Extras<T> {

    private final Entry<T> entry;
    @Getter
    private Bounds<T> bounds;

    public <N extends Number> void setBounds(N min, N max) {
        if (!ClassUtils.isAssignable(entry.getType(), min.getClass()) || !ClassUtils.isAssignable(entry.getType(), max.getClass())) {
            throw new IllegalArgumentException();
        }
        bounds = new Bounds<>((T) min, (T) max);
    }

}