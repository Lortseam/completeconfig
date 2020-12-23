package me.lortseam.completeconfig.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class Bounds<T> {

    @Getter
    private final T min;
    @Getter
    private final T max;
    @Getter
    private final boolean slider;

}