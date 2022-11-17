package me.lortseam.completeconfig.gui.cloth.extensions.clothbasicmath;

import me.lortseam.completeconfig.data.ColorEntry;
import me.lortseam.completeconfig.gui.GuiProvider;
import me.lortseam.completeconfig.gui.cloth.ClothConfigGuiExtension;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;
import me.shedaniel.math.Color;
import net.minecraft.text.Text;

import java.util.List;

public final class ClothBasicMathClothConfigGuiExtension implements ClothConfigGuiExtension {

    @Override
    public List<GuiProvider<FieldBuilder<?, ?, ?>>> getProviders() {
        return List.of(GuiProvider.create(ColorEntry.class, (ColorEntry<Color> entry) -> ConfigEntryBuilder.create()
                        .startColorField(entry.getName(), entry.getValue())
                        .setAlphaMode(entry.isAlphaMode())
                        .setDefaultValue(entry.getDefaultValue().getColor())
                        .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                        .setSaveConsumer2(entry::setValue),
                Color.class));
    }

}
