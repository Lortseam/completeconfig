package me.lortseam.completeconfig.extensions.clothbasicmath;

import lombok.experimental.UtilityClass;
import me.lortseam.completeconfig.data.ColorEntry;
import me.lortseam.completeconfig.gui.cloth.GuiRegistry;
import me.shedaniel.math.Color;

@UtilityClass
final class GuiProviders {

    static void register() {
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
