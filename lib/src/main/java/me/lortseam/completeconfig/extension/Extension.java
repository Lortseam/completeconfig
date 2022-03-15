package me.lortseam.completeconfig.extension;

import java.util.Set;

/**
 * The base type for CompleteConfig extensions. Every extension type must extend this interface.
 */
public interface Extension {

    /**
     * Used to register child extensions of this extension.
     *
     * @return child extensions of this extension
     */
    default Set<Class<? extends Extension>> children() {
        return null;
    }

}
