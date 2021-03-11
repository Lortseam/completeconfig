package me.lortseam.completeconfig.gui.cloth;

import lombok.Getter;
import me.lortseam.completeconfig.data.Collection;
import me.lortseam.completeconfig.data.Config;
import me.lortseam.completeconfig.data.Entry;
import me.lortseam.completeconfig.data.text.TranslationIdentifier;
import me.lortseam.completeconfig.gui.ConfigScreenBuilder;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
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
@Environment(EnvType.CLIENT)
public final class ClothConfigScreenBuilder extends ConfigScreenBuilder {

    private final Supplier<ConfigBuilder> supplier;
    @Getter
    private final GuiRegistry registry = new GuiRegistry();

    public ClothConfigScreenBuilder(Supplier<ConfigBuilder> supplier) {
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
        TranslationIdentifier customTitle = config.getTranslation().append("title");
        builder.setTitle(customTitle.exists() ? customTitle.toText() : new TranslatableText("completeconfig.gui.defaultTitle", FabricLoader.getInstance().getModContainer(config.getModID()).get().getMetadata().getName()));
        if (!config.getEntries().isEmpty()) {
            ConfigCategory category = builder.getOrCreateCategory(config.getText());
            for (Entry<?> entry : config.getEntries()) {
                category.addEntry(buildEntry(entry));
            }
        }
        for(Collection collection : config.getCollections()) {
            ConfigCategory category = builder.getOrCreateCategory(collection.getText());
            category.setDescription(() -> collection.getTooltipTranslation().map(lines -> Arrays.stream(lines).map(line -> (StringVisitable) line).toArray(StringVisitable[]::new)));
            for (AbstractConfigListEntry<?> entry : buildCollection(collection)) {
                category.addEntry(entry);
            }
        }
        return builder.build();
    }

    private AbstractConfigListEntry<?> buildEntry(Entry<?> entry) {
        return registry.getProvider(entry).orElseThrow(() -> {
            return new UnsupportedOperationException("Could not generate GUI for field " + entry.getField());
        }).build(entry);
    }

    private List<AbstractConfigListEntry> buildCollection(Collection collection) {
        List<AbstractConfigListEntry> collectionGui = new ArrayList<>();
        for (Entry<?> entry : collection.getEntries()) {
            collectionGui.add(buildEntry(entry));
        }
        for (Collection subCollection : collection.getCollections()) {
            SubCategoryBuilder subBuilder = ConfigEntryBuilder.create().startSubCategory(subCollection.getText());
            subBuilder.setTooltip(subCollection.getTooltipTranslation());
            subBuilder.addAll(buildCollection(subCollection));
            collectionGui.add(subBuilder.build());
        }
        return collectionGui;
    }

}
