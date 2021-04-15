package me.lortseam.completeconfig.extensions.clothbasicmath;

import me.lortseam.completeconfig.data.ColorEntry;
import me.lortseam.completeconfig.extensions.ConfigExtensionPattern;
import me.lortseam.completeconfig.gui.cloth.GuiProviderRegistration;
import me.lortseam.completeconfig.gui.cloth.GuiRegistry;
import me.shedaniel.math.Color;

public final class ClothBasicMathClientExtension implements ConfigExtensionPattern {

    ClothBasicMathClientExtension() {
        dependOn("cloth-config2", () -> GuiRegistry.addGlobal(new GuiProviderRegistration<>((ColorEntry<Color> entry) -> GuiRegistry.build(
                builder -> builder
                        .startColorField(entry.getText(), entry.getValue())
                        .setAlphaMode(entry.isAlphaMode())
                        .setDefaultValue(entry.getDefaultValue().getColor())
                        .setTooltip(entry.getTooltip())
                        .setSaveConsumer2(entry::setValue),
                entry.requiresRestart()
        ), ColorEntry.class, Color.class)));
    }

}
