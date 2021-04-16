package me.lortseam.completeconfig.extensions;

import me.lortseam.completeconfig.gui.cloth.Provider;

import java.util.Collection;

public interface GuiExtension extends Extension {

    default Collection<Provider> getProviders() {
        return null;
    }

}
