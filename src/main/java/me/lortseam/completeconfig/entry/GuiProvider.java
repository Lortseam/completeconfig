package me.lortseam.completeconfig.entry;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;

import java.util.function.Consumer;

@FunctionalInterface
public interface GuiProvider<T> {

    AbstractConfigListEntry<T> build(String translationKey, T value, T defaultValue, Consumer<T> saveConsumer);

}