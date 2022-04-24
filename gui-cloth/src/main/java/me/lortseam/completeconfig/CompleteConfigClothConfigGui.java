package me.lortseam.completeconfig;

import me.lortseam.completeconfig.extensions.clothbasicmath.ClothBasicMathClothConfigGuiExtension;
import me.lortseam.completeconfig.extensions.clothconfig.ClothConfigClothConfigGuiExtension;
import me.lortseam.completeconfig.extensions.minecraft.MinecraftClothConfigGuiExtension;
import me.lortseam.completeconfig.gui.cloth.ClothConfigGuiExtension;
import net.fabricmc.api.EnvType;

import java.util.Map;

public final class CompleteConfigClothConfigGui implements CompleteConfigInitializer {

    @Override
    public void onInitializeCompleteConfig() {
        CompleteConfig.registerExtensionType(ClothConfigGuiExtension.class, EnvType.CLIENT, "cloth-config");
    }

    public static final class Extender implements CompleteConfigExtender {

        @Override
        public Map<String, Class<? extends Extension>> getProvidedExtensions() {
            return Map.of(
                    "minecraft", MinecraftClothConfigGuiExtension.class,
                    "cloth-basic-math", ClothBasicMathClothConfigGuiExtension.class,
                    "cloth-config", ClothConfigClothConfigGuiExtension.class
            );
        }

    }

}
