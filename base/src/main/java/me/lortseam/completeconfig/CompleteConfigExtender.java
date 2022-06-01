package me.lortseam.completeconfig;

import java.util.Collection;
import java.util.Map;

/**
 * Used for the {@code completeconfig-extender} entrypoint.
 */
public interface CompleteConfigExtender {

    /**
     * Used to register extensions of this entrypoint's mod.
     *
     * @return a collection of extensions
     */
    default Collection<Class<? extends Extension>> getExtensions() {
        return null;
    }

    /**
     * Used to register extensions for other mods.
     *
     * @return a map where the key is the mod ID and the value is the associated extension
     */
    default Map<String, Class<? extends Extension>> getProvidedExtensions() {
        return null;
    }

}
