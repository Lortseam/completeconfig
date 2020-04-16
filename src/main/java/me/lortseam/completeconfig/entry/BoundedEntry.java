package me.lortseam.completeconfig.entry;

import lombok.Getter;
import me.lortseam.completeconfig.api.ConfigEntryContainer;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;

import java.lang.reflect.Field;
import java.util.function.Consumer;

//TODO: Bound auch beim Einlesen aus JSON beachten
public class BoundedEntry<T extends Number> extends Entry<T> {

    @Getter
    private final T min, max;

    public BoundedEntry(Field field, Class<T> type, ConfigEntryContainer parentObject, String translationKey, T min, T max) {
        super(field, type, parentObject, translationKey);
        this.min = min;
        this.max = max;
    }

    @FunctionalInterface
    public interface GuiProvider<T> {

        AbstractConfigListEntry build(String translationKey, T value, T min, T max, T defaultValue, Consumer<T> saveConsumer);

    }

}