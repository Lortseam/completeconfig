package me.lortseam.completeconfig;

import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.Objects;

public final class CompleteConfig {

    public static void registerGlobalTypeSerializers(TypeSerializerCollection typeSerializers) {
        ConfigSource.registerGlobalTypeSerializers(Objects.requireNonNull(typeSerializers));
    }

}
