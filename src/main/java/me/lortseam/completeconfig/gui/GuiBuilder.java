package me.lortseam.completeconfig.gui;

import lombok.Getter;
import lombok.Setter;
import me.lortseam.completeconfig.Config;
import me.lortseam.completeconfig.ConfigManager;
import me.lortseam.completeconfig.collection.Collection;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class GuiBuilder {

    private final ConfigManager manager;
    private final Config config;
    @Setter
    private Supplier<ConfigBuilder> supplier = ConfigBuilder::create;
    @Getter
    private final GuiRegistry registry = new GuiRegistry();

    public GuiBuilder(ConfigManager manager, Config config) {
        this.manager = manager;
        this.config = config;
    }

    public Screen buildScreen(Screen parentScreen, Runnable savingRunnable) {
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
        collection.getEntries().values().forEach(entry -> collectionGui.add(((Optional<GuiProvider>) manager.getGuiRegistry().getProvider(entry)).orElseGet(() -> {
            throw new UnsupportedOperationException("Could not find gui provider for field " + entry.getField());
        }).build(entry.getText(), entry.getField(), entry.getValue(), entry.getDefaultValue(), entry.getTooltip(), entry.getExtras(), entry::setValue)));
        collection.getCollections().values().forEach(c -> {
            SubCategoryBuilder subBuilder = ConfigEntryBuilder.create().startSubCategory(c.getText());
            subBuilder.addAll(buildCollection(c));
            collectionGui.add(subBuilder.build());
        });
        return collectionGui;
    }
    
}
