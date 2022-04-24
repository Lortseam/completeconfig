package me.lortseam.completeconfig;

import java.util.Map;
import java.util.Set;

/**
 * Used for the {@code completeconfig-extender} entrypoint.
 */
public interface CompleteConfigExtender {

    default Set<Class<? extends Extension>> getExtensions() {
        return null;
    }

    default Map<String, Class<? extends Extension>> getProvidedExtensions() {
        return null;
    }

}
