package me.lortseam.completeconfig.gui.coat;

import de.siphalor.coat.list.entry.ConfigCategoryConfigEntry;
import me.lortseam.completeconfig.Extension;
import me.lortseam.completeconfig.gui.GuiProvider;

import java.util.Collection;

public interface CoatGuiExtension extends Extension {

    /**
     * Used to register global GUI providers.
     *
     * @return a collection of GUI providers
     */
    default Collection<GuiProvider<ConfigCategoryConfigEntry<?>>> getProviders() {
        return null;
    }

}
