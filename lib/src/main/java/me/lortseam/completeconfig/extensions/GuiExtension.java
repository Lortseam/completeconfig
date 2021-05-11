package me.lortseam.completeconfig.extensions;

import me.lortseam.completeconfig.gui.cloth.GuiProvider;

import java.util.Collection;

public interface GuiExtension extends Extension {

    default Collection<GuiProvider> getProviders() {
        return null;
    }

}
