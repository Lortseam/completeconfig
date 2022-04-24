package me.lortseam.completeconfig.data.extension;

import me.lortseam.completeconfig.data.transform.Transformation;
import me.lortseam.completeconfig.Extension;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

/**
 * The data extension type for both client and server environment.
 */
public interface DataExtension extends Extension {

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
