package me.lortseam.completeconfig.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public final class NumberUtils {

    public static int compare(Number first, Number second) {
        return new BigDecimal(first.toString()).compareTo(new BigDecimal(second.toString()));
    }

    public static boolean isPositive(Number number) {
        return compare(number, 0) > 0;
    }

}
