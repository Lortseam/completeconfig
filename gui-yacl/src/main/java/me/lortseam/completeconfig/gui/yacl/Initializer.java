package me.lortseam.completeconfig.gui.yacl;

import me.lortseam.completeconfig.CompleteConfig;
import me.lortseam.completeconfig.CompleteConfigInitializer;
import net.fabricmc.api.EnvType;

public final class Initializer implements CompleteConfigInitializer {

    @Override
    public void onInitializeCompleteConfig() {
        CompleteConfig.registerExtensionType(YaclGuiExtension.class, EnvType.CLIENT, "yet-another-config-lib");
    }

}
