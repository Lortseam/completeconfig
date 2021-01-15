package me.lortseam.completeconfig.extensions;

import me.lortseam.completeconfig.data.entry.Transformation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.Collection;

public interface CompleteConfigExtension {

    default TypeSerializerCollection getTypeSerializers() {
        return null;
    }

    default Collection<Transformation> getTransformations() {
        return null;
    }

    @Environment(EnvType.CLIENT)
    default CompleteConfigGuiExtension gui() {
        return null;
    }

}
