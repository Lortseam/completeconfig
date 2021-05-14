package me.lortseam.completeconfig.extensions;

import me.lortseam.completeconfig.gui.cloth.GuiProvider;

/**
 * The main extension type for extending the Cloth Config screen building process.
 *
 * @see me.lortseam.completeconfig.gui.cloth.ClothConfigScreenBuilder
 */
public interface GuiExtension extends Extension {

    /**
     * Used to register custom {@link GuiProvider}s.
     *
     * @return an array of custom {@link GuiProvider}s
     *
     * @see GuiProvider
     */
    default GuiProvider[] getProviders() {
        return null;
    }

}
