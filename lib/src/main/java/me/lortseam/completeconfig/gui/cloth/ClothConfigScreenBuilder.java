package me.lortseam.completeconfig.gui.cloth;

import lombok.Getter;
import lombok.NonNull;
import me.lortseam.completeconfig.data.Collection;
import me.lortseam.completeconfig.data.Config;
import me.lortseam.completeconfig.data.Entry;
import me.lortseam.completeconfig.text.TranslationKey;
import me.lortseam.completeconfig.gui.ConfigScreenBuilder;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * A screen builder based on the Cloth Config API.
 */
public final class ClothConfigScreenBuilder extends ConfigScreenBuilder {

    private final Supplier<ConfigBuilder> supplier;
    @Getter
    private final GuiProviderRegistry registry = new GuiProviderRegistry();

    public ClothConfigScreenBuilder(@NonNull Supplier<ConfigBuilder> supplier) {
        this.supplier = supplier;
    }

    public ClothConfigScreenBuilder() {
        this(ConfigBuilder::create);
    }

    @Override
    public Screen build(Screen parentScreen, Config config) {
        ConfigBuilder builder = supplier.get()
                .setParentScreen(parentScreen)
                .setSavingRunnable(config::save);
        TranslationKey customTitle = config.getBranchedTranslation().append("title");
        builder.setTitle(customTitle.exists() ? customTitle.toText() : new TranslatableText("completeconfig.gui.defaultTitle", config.getMod().getName()));
        if (!config.getEntries().isEmpty()) {
            ConfigCategory category = builder.getOrCreateCategory(config.getText());
            for (Entry<?> entry : config.getEntries()) {
                category.addEntry(buildEntry(entry));
            }
        }
        for(Collection collection : config.getCollections()) {
            ConfigCategory category = builder.getOrCreateCategory(collection.getText());
            category.setDescription(() -> collection.getTooltip().map(lines -> Arrays.stream(lines).map(line -> (StringVisitable) line).toArray(StringVisitable[]::new)));
            for (AbstractConfigListEntry<?> entry : buildCollection(collection)) {
                category.addEntry(entry);
            }
        }
        return builder.build();
    }

    private AbstractConfigListEntry<?> buildEntry(Entry<?> entry) {
        return registry.findBuilder(entry).orElseThrow(() -> {
            return new UnsupportedOperationException("Could not generate GUI for entry " + entry);
        }).build(entry);
    }

    private List<AbstractConfigListEntry> buildCollection(Collection collection) {
        List<AbstractConfigListEntry> collectionGui = new ArrayList<>();
        for (Entry<?> entry : collection.getEntries()) {
            collectionGui.add(buildEntry(entry));
        }
        for (Collection subCollection : collection.getCollections()) {
            SubCategoryBuilder subBuilder = ConfigEntryBuilder.create()
                    .startSubCategory(subCollection.getText())
                    .setTooltip(subCollection.getTooltip());
            subBuilder.addAll(buildCollection(subCollection));
            collectionGui.add(subBuilder.build());
        }
        return collectionGui;
    }

}
