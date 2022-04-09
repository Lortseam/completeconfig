package me.lortseam.completeconfig;

import me.lortseam.completeconfig.extensions.clothbasicmath.ClothBasicMathClothConfigGuiExtension;
import me.lortseam.completeconfig.extensions.clothconfig.ClothConfigClothConfigGuiExtension;
import me.lortseam.completeconfig.extensions.minecraft.MinecraftClothConfigGuiExtension;
import me.lortseam.completeconfig.gui.cloth.ClothConfigGuiExtension;
import net.fabricmc.api.EnvType;

public final class CompleteConfigClothConfigGui implements CompleteConfigInitializer {

    @Override
    public void onInitializeCompleteConfig() {
        CompleteConfig.registerExtensionType(ClothConfigGuiExtension.class, EnvType.CLIENT, "cloth-config");
        CompleteConfig.registerExtension(MinecraftClothConfigGuiExtension.class);
        CompleteConfig.registerExtension("cloth-basic-math", ClothBasicMathClothConfigGuiExtension.class);
        CompleteConfig.registerExtension("cloth-config", ClothConfigClothConfigGuiExtension.class);
    }

}
