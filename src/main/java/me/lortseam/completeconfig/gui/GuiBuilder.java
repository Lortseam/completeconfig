package me.lortseam.completeconfig.gui;

import lombok.Getter;
import lombok.Setter;
import me.lortseam.completeconfig.CompleteConfig;
import me.lortseam.completeconfig.Config;
import me.lortseam.completeconfig.collection.Collection;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class GuiBuilder {

    private static String joinIDs(String... ids) {
        return String.join(".", ids);
    }

    private final String modID;
    private final Config config;
    @Setter
    private Supplier<ConfigBuilder> supplier = ConfigBuilder::create;
    @Getter
    private final GuiRegistry registry = new GuiRegistry();

    public GuiBuilder(String modID, Config config) {
        this.modID = modID;
        this.config = config;
    }

    private String buildTranslationKey(String... ids) {
        return joinIDs("config", modID, joinIDs(ids));
    }

    public Screen buildScreen(Screen parent, Runnable savingRunnable) {
        ConfigBuilder builder = supplier.get();
        builder.setParentScreen(parent)
                .setTitle(new TranslatableText(buildTranslationKey("title")))
                .setSavingRunnable(savingRunnable);
        config.forEach((categoryID, category) -> {
            ConfigCategory configCategory = builder.getOrCreateCategory(new TranslatableText(buildTranslationKey(categoryID)));
            for (AbstractConfigListEntry entry : buildCollection(categoryID, category)) {
                configCategory.addEntry(entry);
            }
        });
        return builder.build();
    }

    private List<AbstractConfigListEntry> buildCollection(String parentID, Collection collection) {
        List<AbstractConfigListEntry> list = new ArrayList<>();
        collection.getEntries().forEach((entryID, entry) -> {
            String translationKey = entry.getCustomTranslationKey() != null ? buildTranslationKey(entry.getCustomTranslationKey()) : buildTranslationKey(parentID, entryID);
            String[] tooltipKeys = entry.getCustomTooltipKeys();
            if (tooltipKeys != null) {
                tooltipKeys = Arrays.stream(tooltipKeys).map(this::buildTranslationKey).toArray(String[]::new);
            } else {
                String defaultTooltipKey = joinIDs(translationKey, "tooltip");
                if (I18n.hasTranslation(defaultTooltipKey)) {
                    tooltipKeys = new String[] {defaultTooltipKey};
                } else {
                    for(int i = 0;; i++) {
                        String key = joinIDs(defaultTooltipKey, String.valueOf(i));
                        if(I18n.hasTranslation(key)) {
                            if (tooltipKeys == null) {
                                tooltipKeys = new String[]{key};
                            } else {
                                tooltipKeys = ArrayUtils.add(tooltipKeys, key);
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
            list.add(((Optional<GuiProvider>) CompleteConfig.getManager(modID).get().getGuiRegistry().getProvider(entry)).orElseGet(() -> {
                throw new UnsupportedOperationException("Could not find gui provider for field " + entry.getField());
            }).build(new TranslatableText(translationKey), entry.getField(), entry.getValue(), entry.getDefaultValue(), tooltipKeys != null ? Optional.of(Arrays.stream(tooltipKeys).map(TranslatableText::new).toArray(Text[]::new)) : Optional.empty(), entry.getExtras(), entry::setValue));
        });
        collection.getCollections().forEach((subcategoryID, c) -> {
            String id = joinIDs(parentID, subcategoryID);
            SubCategoryBuilder subBuilder = ConfigEntryBuilder.create().startSubCategory(new TranslatableText(buildTranslationKey(id)));
            subBuilder.addAll(buildCollection(id, c));
            list.add(subBuilder.build());
        });
        return list;
    }
    
}
