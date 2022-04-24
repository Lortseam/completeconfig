package me.lortseam.completeconfig.extensions.minecraft;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.lortseam.completeconfig.data.ColorEntry;
import me.lortseam.completeconfig.data.transform.Transformation;
import me.lortseam.completeconfig.data.extension.ClientDataExtension;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.TextColor;
import org.spongepowered.configurate.serialize.CoercionFailedException;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftClientDataExtension implements ClientDataExtension {

    @Override
    public TypeSerializerCollection getTypeSerializers() {
        return TypeSerializerCollection.builder()
                .registerExact(TypeSerializer.of(TextColor.class, (v, pass) -> v.getRgb(), v -> {
                    if (v instanceof Integer) {
                        return TextColor.fromRgb((Integer) v);
                    }
                    throw new CoercionFailedException(v, TextColor.class.getSimpleName());
                }))
                .registerExact(TypeSerializer.of(InputUtil.Key.class, (v, pass) -> v.getTranslationKey(), v -> {
                    if(v instanceof String) {
                        try {
                            return InputUtil.fromTranslationKey((String) v);
                        } catch(Exception e) {
                            throw new SerializationException(e);
                        }
                    }
                    throw new CoercionFailedException(v, InputUtil.Key.class.getSimpleName());
                }))
                .build();
    }

    @Override
    public Transformation[] getTransformations() {
        return new Transformation[] {
                Transformation.builder().byType(TextColor.class).transforms(origin -> new ColorEntry<>(origin, false))
        };
    }

}
