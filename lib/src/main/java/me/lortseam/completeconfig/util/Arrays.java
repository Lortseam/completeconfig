package me.lortseam.completeconfig.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class Arrays {

    public static <T> T[] requireNonEmpty(T[] array, String name) {
        if (array.length == 0) {
            throw new IllegalArgumentException(name + " must not be empty");
        }
        return array;
    }

}
