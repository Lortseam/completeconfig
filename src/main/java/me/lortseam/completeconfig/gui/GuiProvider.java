package me.lortseam.completeconfig.gui;

import me.lortseam.completeconfig.entry.Extras;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;

import java.lang.reflect.Field;
import java.util.function.Consumer;

@FunctionalInterface
public interface GuiProvider<T> {

    //TODO: Replace translationKey with Text
    AbstractConfigListEntry<T> build(String translationKey, Field field, T value, T defaultValue, Extras<T> extras, Consumer<T> saveConsumer);

}
