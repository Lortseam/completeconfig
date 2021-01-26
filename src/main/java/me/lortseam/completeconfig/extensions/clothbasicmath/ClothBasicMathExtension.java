package me.lortseam.completeconfig.extensions.clothbasicmath;

import com.google.common.collect.ImmutableList;
import me.lortseam.completeconfig.data.ColorEntry;
import me.lortseam.completeconfig.data.entry.Transformation;
import me.lortseam.completeconfig.extensions.CompleteConfigExtension;
import me.shedaniel.math.Color;
import net.fabricmc.loader.api.FabricLoader;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.Collection;

public final class ClothBasicMathExtension implements CompleteConfigExtension {

    private static final TypeSerializerCollection SERIALIZERS = TypeSerializerCollection.builder()
            .registerExact(ColorSerializer.INSTANCE)
            .build();

    private ClothBasicMathExtension() {
        if (FabricLoader.getInstance().isModLoaded("cloth-config2")) {
            GuiProviders.register();
        }
    }

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

}
