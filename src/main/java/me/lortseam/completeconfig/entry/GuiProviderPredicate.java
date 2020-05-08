package me.lortseam.completeconfig.entry;

import java.lang.reflect.Field;

@FunctionalInterface
public interface GuiProviderPredicate<T> {

    //TODO: Also provide object of field (T)
    boolean test(Field field, Class<T> type, Extras<T> extras);

}
