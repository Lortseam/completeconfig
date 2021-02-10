package me.lortseam.completeconfig.extensions.confabricate;

import ca.stellardrift.confabricate.typeserializers.MinecraftSerializers;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.lortseam.completeconfig.extensions.CompleteConfigExtension;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfabricateExtension implements CompleteConfigExtension {

    @Override
    public TypeSerializerCollection getTypeSerializers() {
        return MinecraftSerializers.collection();
    }

}
