package me.lortseam.completeconfig.extensions.clothbasicmath;

import com.google.common.collect.ImmutableList;
import me.lortseam.completeconfig.data.ColorEntry;
import me.lortseam.completeconfig.data.entry.Transformation;
import me.lortseam.completeconfig.extensions.CompleteConfigExtension;
import me.lortseam.completeconfig.gui.cloth.GuiRegistry;
import me.shedaniel.math.Color;
import net.fabricmc.loader.api.FabricLoader;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.Collection;

public final class ClothBasicMathExtension implements CompleteConfigExtension {

    private ClothBasicMathExtension() {
        if (FabricLoader.getInstance().isModLoaded("cloth-config2")) {
            GuiRegistry.addGlobalRegistrar(registry -> registry.registerColorProvider((ColorEntry<Color> entry) -> GuiRegistry.build(
                    builder -> builder
                            .startColorField(entry.getText(), entry.getValue())
                            .setAlphaMode(entry.isAlphaMode())
                            .setDefaultValue(entry.getDefaultValue().getColor())
                            .setTooltip(entry.getTooltip())
                            .setSaveConsumer2(entry::setValue),
                    entry.requiresRestart()
            ), true, Color.class));
        }
    }

    @Override
    public TypeSerializerCollection getTypeSerializers() {
        return TypeSerializerCollection.builder()
                .registerExact(ColorSerializer.INSTANCE)
                .build();
    }

    @Override
    public Collection<Transformation> getTransformations() {
        return ImmutableList.of(
                Transformation.ofType(Color.class, origin -> new ColorEntry<>(origin, true))
        );
    }

}
