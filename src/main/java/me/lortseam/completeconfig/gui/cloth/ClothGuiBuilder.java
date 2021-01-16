package me.lortseam.completeconfig.gui.cloth;

import me.lortseam.completeconfig.data.Collection;
import me.lortseam.completeconfig.data.Config;
import me.lortseam.completeconfig.data.Entry;
import me.lortseam.completeconfig.data.text.TranslationIdentifier;
import me.lortseam.completeconfig.gui.GuiBuilder;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ClothGuiBuilder implements GuiBuilder {

    private final Supplier<ConfigBuilder> supplier;

    public ClothGuiBuilder(Supplier<ConfigBuilder> supplier) {
        this.supplier = supplier;
    }

    public ClothGuiBuilder() {
        this(ConfigBuilder::create);
    }

    @Override
    public Screen buildScreen(Screen parentScreen, Config config, Runnable savingRunnable) {
        ConfigBuilder builder = supplier.get()
                .setParentScreen(parentScreen)
                .setSavingRunnable(savingRunnable);
        TranslationIdentifier customTitle = config.getTranslation().append("title");
        builder.setTitle(customTitle.exists() ? customTitle.translate() : new TranslatableText("completeconfig.gui.defaultTitle", FabricLoader.getInstance().getModContainer(config.getModID()).get().getMetadata().getName()));
        for(Collection collection : config.values()) {
            ConfigCategory category = builder.getOrCreateCategory(collection.getText());
            for (AbstractConfigListEntry<?> entry : buildCollection(collection)) {
                category.addEntry(entry);
            }
        }
        return builder.build();
    }

    private List<AbstractConfigListEntry> buildCollection(Collection collection) {
        List<AbstractConfigListEntry> collectionGui = new ArrayList<>();
        for (Entry entry : collection.getEntries().values()) {
            collectionGui.add((GuiRegistry.getInstance().getProvider(entry)).orElseThrow(() -> {
                return new UnsupportedOperationException("Could not find GUI provider for field " + entry.getField());
            }).build(entry));
        }
        for (Collection c : collection.getCollections().values()) {
            SubCategoryBuilder subBuilder = ConfigEntryBuilder.create().startSubCategory(c.getText());
            subBuilder.addAll(buildCollection(c));
            collectionGui.add(subBuilder.build());
        }
        return collectionGui;
    }

}
