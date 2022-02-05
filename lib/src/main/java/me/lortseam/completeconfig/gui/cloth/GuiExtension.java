package me.lortseam.completeconfig.gui.cloth;

import me.lortseam.completeconfig.Extension;

/**
 * The main extension type to extend the Cloth Config screen building process.
 *
 * @see me.lortseam.completeconfig.gui.cloth.ClothConfigScreenBuilder
 */
public interface GuiExtension extends Extension {

    /**
     * Used to register global GUI providers.
     *
     * @return an array of custom GUI providers
     *
     * @see me.lortseam.completeconfig.gui.cloth.ClothConfigScreenBuilder#register(GuiProvider...)
     */
    default GuiProvider[] getProviders() {
        return null;
    }

}
