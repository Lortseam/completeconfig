package me.lortseam.completeconfig.extensions.clothbasicmath;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.lortseam.completeconfig.data.ColorEntry;
import me.lortseam.completeconfig.data.transform.Transformation;
import me.lortseam.completeconfig.data.extension.DataExtension;
import me.shedaniel.math.Color;
import org.spongepowered.configurate.serialize.CoercionFailedException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.Collection;
import java.util.Collections;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClothBasicMathExtension implements DataExtension {

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
    public Collection<Transformation> getTransformations() {
        return Collections.singleton(
                new Transformation(Transformation.filter().byType(Color.class), origin -> new ColorEntry<>(origin, true))
        );
    }

}
