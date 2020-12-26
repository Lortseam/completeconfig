package me.lortseam.completeconfig.gui.cloth;

import me.lortseam.completeconfig.data.Entry;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface GuiProvider<T> {

    AbstractConfigListEntry<T> build(Entry<T> entry);

}
