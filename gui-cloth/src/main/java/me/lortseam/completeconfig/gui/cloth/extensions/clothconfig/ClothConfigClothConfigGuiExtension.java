package me.lortseam.completeconfig.gui.cloth.extensions.clothconfig;

import me.lortseam.completeconfig.data.Entry;
import me.lortseam.completeconfig.gui.GuiProvider;
import me.lortseam.completeconfig.gui.cloth.ClothConfigGuiExtension;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.ModifierKeyCode;
import net.minecraft.text.Text;

import java.util.List;

public final class ClothConfigClothConfigGuiExtension implements ClothConfigGuiExtension {

    @Override
    public List<GuiProvider<AbstractConfigListEntry<?>>> getProviders() {
        return List.of(GuiProvider.create((Entry<ModifierKeyCode> entry) -> ConfigEntryBuilder.create()
                        .startModifierKeyCodeField(entry.getName(), entry.getValue())
                        .setDefaultValue(entry.getDefaultValue())
                        .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                        .setModifierSaveConsumer(entry::setValue)
                        .build(),
                ModifierKeyCode.class));
    }

}
