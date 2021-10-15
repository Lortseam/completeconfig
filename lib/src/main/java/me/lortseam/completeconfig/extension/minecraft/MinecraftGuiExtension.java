package me.lortseam.completeconfig.extension.minecraft;

import me.lortseam.completeconfig.data.Entry;
import me.lortseam.completeconfig.extension.clothconfig.GuiExtension;
import me.lortseam.completeconfig.gui.cloth.GuiProvider;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.util.InputUtil;

public final class MinecraftGuiExtension implements GuiExtension {

    @Override
    public GuiProvider[] getProviders() {
        return new GuiProvider[] {
                GuiProvider.create((Entry<InputUtil.Key> entry) -> ConfigEntryBuilder.create()
                        .startKeyCodeField(entry.getText(), entry.getValue())
                        .setDefaultValue(entry.getDefaultValue())
                        .setTooltip(entry.getTooltip())
                        .setSaveConsumer(entry::setValue),
                        InputUtil.Key.class)
        };
    }

}
