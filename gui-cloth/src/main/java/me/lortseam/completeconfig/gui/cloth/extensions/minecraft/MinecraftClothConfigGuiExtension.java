package me.lortseam.completeconfig.gui.cloth.extensions.minecraft;

import me.lortseam.completeconfig.data.ColorEntry;
import me.lortseam.completeconfig.data.Entry;
import me.lortseam.completeconfig.gui.GuiProvider;
import me.lortseam.completeconfig.gui.cloth.ClothConfigGuiExtension;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import java.util.List;

public final class MinecraftClothConfigGuiExtension implements ClothConfigGuiExtension {

    @Override
    public List<GuiProvider<FieldBuilder<?, ?, ?>>> getProviders() {
        return List.of(GuiProvider.create(ColorEntry.class, (ColorEntry<TextColor> entry) -> ConfigEntryBuilder.create()
                                .startColorField(entry.getName(), entry.getValue())
                                .setDefaultValue(entry.getDefaultValue())
                                .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                                .setSaveConsumer3(entry::setValue),
                        entry -> !entry.isAlphaMode(), TextColor.class),
                GuiProvider.create((Entry<InputUtil.Key> entry) -> ConfigEntryBuilder.create()
                                .startKeyCodeField(entry.getName(), entry.getValue())
                                .setDefaultValue(entry.getDefaultValue())
                                .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                                .setKeySaveConsumer(entry::setValue),
                        InputUtil.Key.class));
    }

}
