package me.lortseam.completeconfig.extensions.clothbasicmath;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.lortseam.completeconfig.data.ColorEntry;
import me.lortseam.completeconfig.data.entry.Transformation;
import me.lortseam.completeconfig.extensions.CompleteConfigExtension;
import me.lortseam.completeconfig.extensions.ConfigExtensionPattern;
import me.shedaniel.math.Color;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.Collection;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClothBasicMathExtension implements CompleteConfigExtension {

    @Override
    public TypeSerializerCollection getTypeSerializers() {
        return TypeSerializerCollection.builder()
                .registerExact(ColorSerializer.INSTANCE)
                .build();
    }

    @Override
    public Collection<Transformation> getTransformations() {
        return ImmutableList.of(
                Transformation.byType(Color.class).transforms(origin -> new ColorEntry<>(origin, true))
        );
    }

    @Override
    public ConfigExtensionPattern client() {
        return new ClothBasicMathClientExtension();
    }

}
