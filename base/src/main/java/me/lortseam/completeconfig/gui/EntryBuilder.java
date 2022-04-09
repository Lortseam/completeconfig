package me.lortseam.completeconfig.gui;

import me.lortseam.completeconfig.data.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface EntryBuilder<E extends Entry<?>, T> {

    T build(E entry);

}
