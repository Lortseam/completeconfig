package me.lortseam.completeconfig.extensions;

import me.lortseam.completeconfig.data.entry.Transformation;
import me.lortseam.completeconfig.gui.cloth.extensions.CompleteConfigGuiExtension;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.Collection;

public interface CompleteConfigExtension {

    default TypeSerializerCollection getTypeSerializers() {
        return null;
    }

    default Collection<Transformation> getTransformations() {
        return null;
    }

    default CompleteConfigGuiExtension gui() {
        return null;
    }

}
