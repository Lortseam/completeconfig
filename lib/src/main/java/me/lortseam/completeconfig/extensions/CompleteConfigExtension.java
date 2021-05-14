package me.lortseam.completeconfig.extensions;

import me.lortseam.completeconfig.data.transform.Transformation;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

/**
 * The main CompleteConfig extension type. Used for the {@code completeconfig-extension} entrypoint.
 */
public interface CompleteConfigExtension extends Extension {

    /**
     * Used to register custom type serializers for config entries.
     *
     * @return a collection of custom type serializers
     */
    default TypeSerializerCollection getTypeSerializers() {
        return null;
    }

    /**
     * Used to register custom entry transformations.
     *
     * @return an array of custom transformations
     *
     * @see Transformation
     */
    default Transformation[] getTransformations() {
        return null;
    }

}
