package me.lortseam.completeconfig.extensions.clothbasicmath;

import me.lortseam.completeconfig.data.ColorEntry;
import me.lortseam.completeconfig.extensions.ConfigExtensionPattern;
import me.lortseam.completeconfig.gui.cloth.Provider;
import me.lortseam.completeconfig.gui.cloth.GuiRegistry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.math.Color;

public final class ClothBasicMathClientExtension implements ConfigExtensionPattern {

    ClothBasicMathClientExtension() {
        dependOn("cloth-config2", () -> GuiRegistry.addGlobal(Provider.create(ColorEntry.class, (ColorEntry<Color> entry) -> ConfigEntryBuilder.create()
                        .startColorField(entry.getText(), entry.getValue())
                        .setAlphaMode(entry.isAlphaMode())
                        .setDefaultValue(entry.getDefaultValue().getColor())
                        .setTooltip(entry.getTooltip())
                        .setSaveConsumer2(entry::setValue),
                ColorEntry.class, Color.class)));
    }

}
