package me.lortseam.completeconfig.gui;

import me.lortseam.completeconfig.entry.Extras;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.text.Text;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Consumer;

@FunctionalInterface
public interface GuiProvider<T> {

    //TODO: Optional should not be used for an argument
    AbstractConfigListEntry<T> build(Text text, Field field, T value, T defaultValue, Optional<Text[]> tooltip, Extras<T> extras, Consumer<T> saveConsumer, boolean requiresRestart);

}
