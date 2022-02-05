package me.lortseam.completeconfig.gui.cloth;

import lombok.RequiredArgsConstructor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Stores global and screen builder specific GUI providers.
 */
@Environment(EnvType.CLIENT)
@RequiredArgsConstructor
public final class GuiProviderRegistry {

    private final ClothConfigScreenBuilder parent;

    /**
     * Registers one or more custom GUI providers.
     *
     * @param providers the custom GUI providers
     *
     * @see GuiExtension#getProviders()
     *
     * @deprecated Use {@link ClothConfigScreenBuilder#register(GuiProvider...)}
     */
    @Deprecated
    public void add(GuiProvider... providers) {
        parent.register(providers);
    }

}
