package me.lortseam.completeconfig.gui.cloth;

import me.lortseam.completeconfig.data.Entry;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface EntryBuilder<E extends Entry<?>> {

    FieldBuilder<?, ?> apply(E entry);

    default AbstractConfigListEntry<?> build(E entry) {
        FieldBuilder<?, ?> builder = apply(entry);
        builder.requireRestart(entry.requiresRestart());
        return builder.build();
    }

}
