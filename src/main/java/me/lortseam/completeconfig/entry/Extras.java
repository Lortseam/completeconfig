package me.lortseam.completeconfig.entry;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class Extras<T> {

    @Getter
    private final Bounds<T> bounds;

}