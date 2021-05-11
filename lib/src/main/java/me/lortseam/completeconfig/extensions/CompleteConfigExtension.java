package me.lortseam.completeconfig.extensions;

import me.lortseam.completeconfig.data.entry.Transformation;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

public interface CompleteConfigExtension extends Extension {

    default TypeSerializerCollection getTypeSerializers() {
        return null;
    }

    default Transformation[] getTransformations() {
        return null;
    }

}
