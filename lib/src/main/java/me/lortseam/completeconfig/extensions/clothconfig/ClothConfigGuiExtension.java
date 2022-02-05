package me.lortseam.completeconfig.extensions.clothconfig;

import me.lortseam.completeconfig.data.Entry;
import me.lortseam.completeconfig.gui.cloth.GuiExtension;
import me.lortseam.completeconfig.gui.cloth.GuiProvider;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.ModifierKeyCode;

public final class ClothConfigGuiExtension implements GuiExtension {

    @Override
    public GuiProvider[] getProviders() {
        return new GuiProvider[] {
                GuiProvider.create((Entry<ModifierKeyCode> entry) -> ConfigEntryBuilder.create()
                        .startModifierKeyCodeField(entry.getText(), entry.getValue())
                        .setDefaultValue(entry.getDefaultValue())
                        .setTooltip(entry.getTooltip())
                        .setModifierSaveConsumer(entry::setValue),
                        ModifierKeyCode.class)
        };
    }

}
