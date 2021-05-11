package me.lortseam.completeconfig.extensions.clothbasicmath;

import me.lortseam.completeconfig.data.ColorEntry;
import me.lortseam.completeconfig.extensions.GuiExtension;
import me.lortseam.completeconfig.gui.cloth.GuiProvider;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.math.Color;

import java.util.Collection;
import java.util.Collections;

final class ClothBasicMathGuiExtension implements GuiExtension {

    @Override
    public Collection<GuiProvider> getProviders() {
        return Collections.singletonList(GuiProvider.create(ColorEntry.class, (ColorEntry<Color> entry) -> ConfigEntryBuilder.create()
                        .startColorField(entry.getText(), entry.getValue())
                        .setAlphaMode(entry.isAlphaMode())
                        .setDefaultValue(entry.getDefaultValue().getColor())
                        .setTooltip(entry.getTooltip())
                        .setSaveConsumer2(entry::setValue),
                Color.class));
    }

}
