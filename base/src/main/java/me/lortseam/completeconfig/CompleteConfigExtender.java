package me.lortseam.completeconfig;

import java.util.Collection;
import java.util.Map;

/**
 * Used for the {@code completeconfig-extender} entrypoint.
 */
public interface CompleteConfigExtender {

    default Collection<Class<? extends Extension>> getExtensions() {
        return null;
    }

    default Map<String, Class<? extends Extension>> getProvidedExtensions() {
        return null;
    }

}
