package me.lortseam.completeconfig.io;

import lombok.experimental.UtilityClass;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextColor;
import org.spongepowered.configurate.serialize.CoercionFailedException;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

@UtilityClass
final class ClientSerializers {

    private static final ScalarSerializer<TextColor> TEXT_COLOR = TypeSerializer.of(TextColor.class, (v, pass) -> v.getRgb(), v -> {
        if (v instanceof Integer) {
            return TextColor.fromRgb((Integer) v);
        }
        throw new CoercionFailedException(v, TextColor.class.getSimpleName());
    });

    @Environment(EnvType.CLIENT)
    final static TypeSerializerCollection COLLECTION = TypeSerializerCollection.builder()
            .registerExact(TEXT_COLOR)
            .build();

}
