package me.lortseam.completeconfig.extensions.minecraft;

import me.lortseam.completeconfig.data.extension.BaseExtension;
import me.lortseam.completeconfig.Extension;

import java.util.Set;

public final class MinecraftExtension implements BaseExtension {

    @Override
    public Set<Class<? extends Extension>> children() {
        return Set.of(MinecraftClientExtension.class, MinecraftClothConfigGuiExtension.class);
    }

}
