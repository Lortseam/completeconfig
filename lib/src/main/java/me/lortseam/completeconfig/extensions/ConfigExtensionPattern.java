package me.lortseam.completeconfig.extensions;

import me.lortseam.completeconfig.data.entry.Transformation;
import net.fabricmc.loader.api.FabricLoader;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.Collection;

public interface ConfigExtensionPattern {

    default TypeSerializerCollection getTypeSerializers() {
        return null;
    }

    default Collection<Transformation> getTransformations() {
        return null;
    }

    default void dependOn(String dependentModID, Runnable runnable) {
        if(!FabricLoader.getInstance().isModLoaded(dependentModID)) return;
        runnable.run();
    }

}
