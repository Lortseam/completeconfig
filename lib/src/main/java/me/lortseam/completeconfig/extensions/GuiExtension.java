package me.lortseam.completeconfig.extensions;

import me.lortseam.completeconfig.gui.cloth.GuiProvider;

/**
 * The main extension type to extend the Cloth Config screen building process.
 *
 * @see me.lortseam.completeconfig.gui.cloth.ClothConfigScreenBuilder
 */
public interface GuiExtension extends Extension {

    /**
     * Used to register custom GUI providers.
     *
     * @return an array of custom GUI providers
     *
     * @see GuiProvider
     */
    default GuiProvider[] getProviders() {
        return null;
    }

}
