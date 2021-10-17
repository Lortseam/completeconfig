package me.lortseam.completeconfig.extension.minecraft;

import me.lortseam.completeconfig.data.ColorEntry;
import me.lortseam.completeconfig.data.Entry;
import me.lortseam.completeconfig.extension.clothconfig.GuiExtension;
import me.lortseam.completeconfig.gui.cloth.GuiProvider;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.TextColor;

public final class MinecraftGuiExtension implements GuiExtension {

    @Override
    public GuiProvider[] getProviders() {
        return new GuiProvider[] {
                GuiProvider.create(ColorEntry.class, (ColorEntry<TextColor> entry) -> ConfigEntryBuilder.create()
                        .startColorField(entry.getText(), entry.getValue())
                        .setDefaultValue(entry.getDefaultValue())
                        .setTooltip(entry.getTooltip())
                        .setSaveConsumer3(entry::setValue),
                        entry -> !entry.isAlphaMode(), TextColor.class),
                GuiProvider.create((Entry<InputUtil.Key> entry) -> ConfigEntryBuilder.create()
                        .startKeyCodeField(entry.getText(), entry.getValue())
                        .setDefaultValue(entry.getDefaultValue())
                        .setTooltip(entry.getTooltip())
                        .setSaveConsumer(entry::setValue),
                        InputUtil.Key.class)
        };
    }

}
