package me.lortseam.completeconfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import lombok.AccessLevel;
import lombok.Getter;
import me.lortseam.completeconfig.api.ConfigCategory;
import me.lortseam.completeconfig.api.ConfigEntryContainer;
import me.lortseam.completeconfig.collection.Collection;
import me.lortseam.completeconfig.entry.Entry;
import me.lortseam.completeconfig.gui.GuiRegistry;
import me.lortseam.completeconfig.serialization.CollectionSerializer;
import me.lortseam.completeconfig.serialization.EntrySerializer;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ConfigManager {

    private static String joinIDs(String... ids) {
        return String.join(".", ids);
    }

    @Getter(AccessLevel.PACKAGE)
    private final String modID;
    private final Path jsonPath;
    private final Config config;
    @Getter
    private final GuiRegistry guiRegistry = new GuiRegistry();
    private Supplier<ConfigBuilder> guiBuilder = ConfigBuilder::create;

    ConfigManager(String modID) {
        this.modID = modID;
        jsonPath = Paths.get(FabricLoader.getInstance().getConfigDir().toString(), modID + ".json");
        config = new Config(load());
    }

    private JsonElement load() {
        if(Files.exists(jsonPath)) {
            try {
                return new Gson().fromJson(new FileReader(jsonPath.toString()), JsonElement.class);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return JsonNull.INSTANCE;
    }

    //TODO: Create own class for validations like these
    private void addListenerToEntry(Entry<?> entry, Method method, ConfigEntryContainer container) {
        //TODO: Add void return type check
        //TODO: Allow listeners without parameters if forceUpdate equals true or listener is defined in different class
        if (method.getParameterCount() != 1 || method.getParameterTypes()[0] != entry.getType()) {
            throw new IllegalArgumentException("Listener method " + method + " has wrong parameter type(s)");
        }
        if (!method.isAccessible()) {
            method.setAccessible(true);
        }
        entry.addListener(method, container);
    }

    //TODO: Löschen
    private Map<String, Entry> findEntries(LinkedHashMap<String, Collection> collections, Class<? extends ConfigEntryContainer> parentClass) {
        Map<String, Entry> entries = new HashMap<>();
        for (Collection collection : collections.values()) {
            entries.putAll(collection.getEntries().entrySet().stream().filter(entry -> entry.getValue().getParentObject().getClass() == parentClass).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
            entries.putAll(findEntries(collection.getCollections(), parentClass));
        }
        return entries;
    }

    public void register(ConfigCategory... categories) {
        for (ConfigCategory category : categories) {
            config.registerTopLevelCategory(category);
        }
    }


    private String buildTranslationKey(String... ids) {
        return joinIDs("config", modID, joinIDs(ids));
    }

    public void setCustomGuiBuilder(Supplier<ConfigBuilder> guiBuilder) {
        this.guiBuilder = guiBuilder;
    }

    //TODO: Rename to buildScreen(Screen parent)
    public Screen getConfigScreen(Screen parentScreen) {
        ConfigBuilder builder = guiBuilder.get();
        builder.setParentScreen(parentScreen)
                .setTitle(new TranslatableText(buildTranslationKey("title")))
                .setSavingRunnable(this::save);
        config.forEach((categoryID, category) -> {
            me.shedaniel.clothconfig2.api.ConfigCategory configCategory = builder.getOrCreateCategory(new TranslatableText(buildTranslationKey(categoryID)));
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
            list.add(guiRegistry.getProvider(entry).build(new TranslatableText(translationKey), entry.getField(), entry.getValue(), entry.getDefaultValue(), tooltipKeys != null ? Optional.of(Arrays.stream(tooltipKeys).map(TranslatableText::new).toArray(Text[]::new)) : Optional.empty(), entry.getExtras(), entry::setValue));
        });
        collection.getCollections().forEach((subcategoryID, c) -> {
            String id = joinIDs(parentID, subcategoryID);
            SubCategoryBuilder subBuilder = ConfigEntryBuilder.create().startSubCategory(new TranslatableText(buildTranslationKey(id)));
            subBuilder.addAll(buildCollection(id, c));
            list.add(subBuilder.build());
        });
        return list;
    }

    public void save() {
        if (!Files.exists(jsonPath)) {
            try {
                Files.createDirectories(jsonPath.getParent());
                Files.createFile(jsonPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try(Writer writer = new FileWriter(jsonPath.toString())) {
            new GsonBuilder()
                    .registerTypeAdapter(CollectionSerializer.TYPE, new CollectionSerializer())
                    .registerTypeAdapter(EntrySerializer.TYPE, new EntrySerializer())
                    .setPrettyPrinting()
                    .create()
                    .toJson(config, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}