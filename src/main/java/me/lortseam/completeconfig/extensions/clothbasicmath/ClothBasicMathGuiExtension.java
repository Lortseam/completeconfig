package me.lortseam.completeconfig.extensions.clothbasicmath;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.lortseam.completeconfig.data.ColorEntry;
import me.lortseam.completeconfig.gui.cloth.GuiRegistry;
import me.lortseam.completeconfig.extensions.CompleteConfigGuiExtension;
import me.shedaniel.math.Color;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ClothBasicMathGuiExtension implements CompleteConfigGuiExtension {

    static final ClothBasicMathGuiExtension INSTANCE = new ClothBasicMathGuiExtension();

    @Override
    public void registerProviders(GuiRegistry registry) {
        registry.registerColorProvider((ColorEntry<Color> entry) -> GuiRegistry.build(
                builder -> builder
                        .startColorField(entry.getText(), entry.getValue())
                        .setAlphaMode(entry.isAlphaMode())
                        .setDefaultValue(entry.getDefaultValue().getColor())
                        .setTooltip(entry.getTooltip())
                        .setSaveConsumer2(entry::setValue),
                entry.requiresRestart()
        ), true, Color.class);
    }

}
