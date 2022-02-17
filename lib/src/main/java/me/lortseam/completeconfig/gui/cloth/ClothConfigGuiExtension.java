package me.lortseam.completeconfig.gui.cloth;

import me.lortseam.completeconfig.Extension;
import me.lortseam.completeconfig.gui.GuiProvider;
import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;

import java.util.List;

public interface ClothConfigGuiExtension extends Extension {

    default List<GuiProvider<FieldBuilder<?, ?>>> getProviders() {
        return null;
    }

}
