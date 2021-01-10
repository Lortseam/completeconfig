package me.lortseam.completeconfig.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.reflect.TypeUtils;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class Extras<T> {

    private final Entry<T> entry;
    @Getter
    private Bounds<T> bounds;
    @Getter
    private EnumOptions enumOptions;

    <N extends Number> void setBounds(N min, N max, boolean slider) {
        if (!TypeUtils.isAssignable(entry.getType(), min.getClass()) || !TypeUtils.isAssignable(entry.getType(), max.getClass())) {
            throw new IllegalArgumentException();
        }
        bounds = new Bounds<>((T) min, (T) max, slider);
    }

    void setEnumOptions(EnumOptions.DisplayType displayType) {
        enumOptions = new EnumOptions(entry, displayType);
    }

}