package me.lortseam.completeconfig.extensions.clothbasicmath;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.lortseam.completeconfig.data.ColorEntry;
import me.lortseam.completeconfig.data.entry.Transformation;
import me.lortseam.completeconfig.extensions.CompleteConfigExtension;
import me.shedaniel.math.Color;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.Collection;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClothBasicMathExtension implements CompleteConfigExtension {

    private static final TypeSerializerCollection SERIALIZERS = TypeSerializerCollection.builder()
            .registerExact(ColorSerializer.INSTANCE)
            .build();

    @Override
    public TypeSerializerCollection getTypeSerializers() {
        return SERIALIZERS;
    }

    @Override
    public Collection<Transformation> getTransformations() {
        return ImmutableList.of(
                Transformation.ofType(Color.class, origin -> new ColorEntry<>(origin, true))
        );
    }

    @Override
    public Gui gui() {
        return ClothBasicMathGuiExtension.INSTANCE;
    }

}
