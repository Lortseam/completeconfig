package me.lortseam.completeconfig.gui.coat;

import de.siphalor.coat.list.entry.ConfigListConfigEntry;
import me.lortseam.completeconfig.Extension;
import me.lortseam.completeconfig.gui.GuiProvider;

import java.util.List;

public interface CoatGuiExtension extends Extension {

    default List<GuiProvider<ConfigListConfigEntry<?>>> getProviders() {
        return null;
    }

}
