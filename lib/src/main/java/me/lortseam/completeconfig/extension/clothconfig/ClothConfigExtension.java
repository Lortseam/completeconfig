package me.lortseam.completeconfig.extension.clothconfig;

import com.google.common.collect.ImmutableSet;
import me.lortseam.completeconfig.extension.BaseExtension;
import me.lortseam.completeconfig.extension.Extension;

import java.util.Set;

public final class ClothConfigExtension implements BaseExtension {

    @Override
    public Set<Class<? extends Extension>> children() {
        return ImmutableSet.of(ClothConfigClientExtension.class, ClothConfigGuiExtension.class);
    }

}
