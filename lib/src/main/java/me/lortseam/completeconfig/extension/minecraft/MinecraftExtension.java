package me.lortseam.completeconfig.extension.minecraft;

import me.lortseam.completeconfig.extension.BaseExtension;
import me.lortseam.completeconfig.extension.Extension;

import java.util.Set;

public final class MinecraftExtension implements BaseExtension {

    @Override
    public Set<Class<? extends Extension>> children() {
        return Set.of(MinecraftClientExtension.class, MinecraftGuiExtension.class);
    }

}
