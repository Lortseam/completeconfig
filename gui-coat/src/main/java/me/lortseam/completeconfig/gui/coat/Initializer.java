package me.lortseam.completeconfig.gui.coat;

import me.lortseam.completeconfig.CompleteConfig;
import me.lortseam.completeconfig.CompleteConfigInitializer;
import net.fabricmc.api.EnvType;

public final class Initializer implements CompleteConfigInitializer {

    @Override
    public void onInitializeCompleteConfig() {
        CompleteConfig.registerExtensionType(CoatGuiExtension.class, EnvType.CLIENT, "coat");
    }

}
