package me.lortseam.completeconfig.gui.cloth;

import me.lortseam.completeconfig.CompleteConfigExtender;
import me.lortseam.completeconfig.Extension;
import me.lortseam.completeconfig.gui.cloth.extensions.clothbasicmath.ClothBasicMathClothConfigGuiExtension;
import me.lortseam.completeconfig.gui.cloth.extensions.clothconfig.ClothConfigClothConfigGuiExtension;
import me.lortseam.completeconfig.gui.cloth.extensions.minecraft.MinecraftClothConfigGuiExtension;

import java.util.Map;

public final class Extender implements CompleteConfigExtender {

    @Override
    public Map<String, Class<? extends Extension>> getProvidedExtensions() {
        return Map.of(
                "minecraft", MinecraftClothConfigGuiExtension.class,
                "cloth-basic-math", ClothBasicMathClothConfigGuiExtension.class,
                "cloth-config", ClothConfigClothConfigGuiExtension.class
        );
    }

}
