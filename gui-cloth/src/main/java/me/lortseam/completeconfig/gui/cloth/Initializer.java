package me.lortseam.completeconfig.gui.cloth;

import me.lortseam.completeconfig.CompleteConfig;
import me.lortseam.completeconfig.CompleteConfigExtender;
import me.lortseam.completeconfig.CompleteConfigInitializer;
import me.lortseam.completeconfig.Extension;
import me.lortseam.completeconfig.gui.cloth.extensions.clothbasicmath.ClothBasicMathClothConfigGuiExtension;
import me.lortseam.completeconfig.gui.cloth.extensions.clothconfig.ClothConfigClothConfigGuiExtension;
import me.lortseam.completeconfig.gui.cloth.extensions.minecraft.MinecraftClothConfigGuiExtension;
import net.fabricmc.api.EnvType;

import java.util.Map;

public final class Initializer implements CompleteConfigInitializer {

    @Override
    public void onInitializeCompleteConfig() {
        CompleteConfig.registerExtensionType(ClothConfigGuiExtension.class, EnvType.CLIENT, "cloth-config");
    }

}
