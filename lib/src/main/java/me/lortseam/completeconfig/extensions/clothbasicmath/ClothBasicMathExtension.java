package me.lortseam.completeconfig.extensions.clothbasicmath;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.lortseam.completeconfig.data.ColorEntry;
import me.lortseam.completeconfig.data.transform.Transformation;
import me.lortseam.completeconfig.data.extension.BaseExtension;
import me.lortseam.completeconfig.Extension;
import me.shedaniel.math.Color;
import org.spongepowered.configurate.serialize.CoercionFailedException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClothBasicMathExtension implements BaseExtension {

    @Override
    public TypeSerializerCollection getTypeSerializers() {
        return TypeSerializerCollection.builder()
                .registerExact(TypeSerializer.of(Color.class, (v, pass) -> v.getColor(), v -> {
                    if (v instanceof Integer) {
                        return Color.ofTransparent((Integer) v);
                    }
                    throw new CoercionFailedException(v, Color.class.getSimpleName());
                }))
                .build();
    }

    @Override
    public Transformation[] getTransformations() {
        return new Transformation[] {
                Transformation.builder().byType(Color.class).transforms(origin -> new ColorEntry<>(origin, true))
        };
    }

    @Override
    public Set<Class<? extends Extension>> children() {
        return Set.of(ClothBasicMathClothConfigGuiExtension.class);
    }

}
