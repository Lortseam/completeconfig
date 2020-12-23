package me.lortseam.completeconfig.gui.cloth;

import lombok.Getter;
import me.lortseam.completeconfig.Config;
import me.lortseam.completeconfig.data.Collection;
import me.lortseam.completeconfig.gui.GuiBuilder;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ClothGuiBuilder implements GuiBuilder {

    private final Supplier<ConfigBuilder> supplier;
    @Getter
    private final GuiRegistry registry = new GuiRegistry();

    public ClothGuiBuilder(Supplier<ConfigBuilder> supplier) {
        this.supplier = supplier;
    }

    public ClothGuiBuilder() {
        this(ConfigBuilder::create);
    }

    @Override
    public Screen buildScreen(Screen parentScreen, Config config, Runnable savingRunnable) {
        ConfigBuilder builder = supplier.get();
        builder.setParentScreen(parentScreen)
                .setTitle(new TranslatableText(config.getModTranslationKey() + ".title"))
                .setSavingRunnable(savingRunnable);
        config.values().forEach(collection -> {
            ConfigCategory configCategory = builder.getOrCreateCategory(collection.getText());
            for (AbstractConfigListEntry<?> entry : buildCollection(collection)) {
                configCategory.addEntry(entry);
            }
        });
        return builder.build();
    }

    private List<AbstractConfigListEntry> buildCollection(Collection collection) {
        List<AbstractConfigListEntry> collectionGui = new ArrayList<>();
        collection.getEntries().values().forEach(entry -> collectionGui.add(((Optional<GuiProvider>) registry.getProvider(entry)).orElseGet(() -> {
            throw new UnsupportedOperationException("Could not find gui provider for field " + entry.getField());
        }).build(entry.getText(), entry.getField(), entry.getValue(), entry.getDefaultValue(), entry.getTooltip(), entry.getExtras(), entry::setValue, entry.requiresRestart())));
        collection.getCollections().values().forEach(c -> {
            SubCategoryBuilder subBuilder = ConfigEntryBuilder.create().startSubCategory(c.getText());
            subBuilder.addAll(buildCollection(c));
            collectionGui.add(subBuilder.build());
        });
        return collectionGui;
    }

}
