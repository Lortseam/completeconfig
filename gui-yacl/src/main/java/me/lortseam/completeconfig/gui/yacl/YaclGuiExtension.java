package me.lortseam.completeconfig.gui.yacl;

import me.lortseam.completeconfig.Extension;
import me.lortseam.completeconfig.gui.GuiProvider;

import java.util.Collection;

public interface YaclGuiExtension extends Extension {

    /**
     * Used to register global GUI providers.
     *
     * @return a collection of GUI providers
     */
    default Collection<GuiProvider<ControllerFunction<?>>> getProviders() {
        return null;
    }

}
