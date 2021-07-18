package me.lortseam.completeconfig.extension.minecraft;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.lortseam.completeconfig.data.ColorEntry;
import me.lortseam.completeconfig.data.transform.Transformation;
import me.lortseam.completeconfig.extension.ClientExtension;
import net.minecraft.text.TextColor;
import org.spongepowered.configurate.serialize.CoercionFailedException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MinecraftClientExtension implements ClientExtension {

    @Override
    public TypeSerializerCollection getTypeSerializers() {
        return TypeSerializerCollection.builder()
                .registerExact(TypeSerializer.of(TextColor.class, (v, pass) -> v.getRgb(), v -> {
                    if (v instanceof Integer) {
                        return TextColor.fromRgb((Integer) v);
                    }
                    throw new CoercionFailedException(v, TextColor.class.getSimpleName());
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
