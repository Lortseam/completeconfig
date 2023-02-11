package me.lortseam.completeconfig.gui.cloth;

import me.lortseam.completeconfig.Extension;
import me.lortseam.completeconfig.gui.GuiProvider;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;

import java.util.Collection;

public interface ClothConfigGuiExtension extends Extension {

    /**
     * Used to register global GUI providers.
     *
     * @return a collection of GUI providers
     */
    default Collection<GuiProvider<AbstractConfigListEntry<?>>> getProviders() {
        return null;
    }

}
