package me.lortseam.completeconfig.entry;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;

import java.util.function.Consumer;

@FunctionalInterface
public interface GuiProvider<T> {

    AbstractConfigListEntry<T> build(String translationKey, Class<T> type, T value, T defaultValue, Entry.Extras<T> extras, Consumer<T> saveConsumer);

}
