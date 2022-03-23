package me.lortseam.completeconfig.extensions.clothconfig;

import me.lortseam.completeconfig.data.extension.BaseExtension;
import me.lortseam.completeconfig.Extension;

import java.util.Set;

public final class ClothConfigExtension implements BaseExtension {

    @Override
    public Set<Class<? extends Extension>> children() {
        return Set.of(ClothConfigClientExtension.class, ClothConfigGuiExtension.class);
    }

}
