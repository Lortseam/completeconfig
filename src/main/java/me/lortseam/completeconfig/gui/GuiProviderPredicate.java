package me.lortseam.completeconfig.gui;

import me.lortseam.completeconfig.entry.Extras;

import java.lang.reflect.Field;
import java.util.Objects;

@FunctionalInterface
public interface GuiProviderPredicate<T> {

    boolean test(Field field, Extras<T> extras);

    default GuiProviderPredicate<T> and(GuiProviderPredicate<T> other) {
        Objects.requireNonNull(other);
        return (field, extras) -> this.test(field, extras) && other.test(field, extras);
    }

}
