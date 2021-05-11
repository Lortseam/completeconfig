package me.lortseam.completeconfig.extensions;

import me.lortseam.completeconfig.gui.cloth.GuiProvider;

public interface GuiExtension extends Extension {

    default GuiProvider[] getProviders() {
        return null;
    }

}
