package me.lortseam.completeconfig.gui.coat;

import me.lortseam.completeconfig.gui.coat.CoatGuiExtension;
import net.fabricmc.api.EnvType;

public final class CompleteConfigCoatGui implements CompleteConfigInitializer {

    @Override
    public void onInitializeCompleteConfig() {
        CompleteConfig.registerExtensionType(CoatGuiExtension.class, EnvType.CLIENT, "coat");
    }

}
