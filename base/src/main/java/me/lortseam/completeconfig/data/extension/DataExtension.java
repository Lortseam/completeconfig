package me.lortseam.completeconfig.data.extension;

import me.lortseam.completeconfig.data.transform.Transformation;
import me.lortseam.completeconfig.Extension;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.Collection;

/**
 * The data extension type for both client and server environment.
 */
public interface DataExtension extends Extension {

    /**
     * Used to register global type serializers for config entries.
     *
     * @return a collection of type serializers
     */
    default TypeSerializerCollection getTypeSerializers() {
        return null;
    }

    /**
     * Used to register entry transformations.
     *
     * @return a collection of transformations
     *
     * @see Transformation
     */
    default Collection<Transformation> getTransformations() {
        return null;
    }

}
