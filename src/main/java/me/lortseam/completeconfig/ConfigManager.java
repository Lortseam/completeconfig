package me.lortseam.completeconfig;

import com.google.common.base.CaseFormat;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import lombok.AccessLevel;
import lombok.Getter;
import me.lortseam.completeconfig.api.ConfigCategory;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigEntryContainer;
import me.lortseam.completeconfig.api.ConfigEntrySaveConsumer;
import me.lortseam.completeconfig.collection.Collection;
import me.lortseam.completeconfig.entry.BoundedEntry;
import me.lortseam.completeconfig.entry.Entry;
import me.lortseam.completeconfig.saveconsumer.SaveConsumer;
import me.lortseam.completeconfig.serialization.CollectionsDeserializer;
import me.lortseam.completeconfig.serialization.EntrySerializer;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

//TODO: Sortierung der Categories, Subcategories und Entrys (Nach Registrierungsreihenfolge oder Alphabet; allgemein und für jeden Container einzeln?)
public class ConfigManager {

    @Getter(AccessLevel.PACKAGE)
    private final String modID;
    private final Path jsonPath;
    private final LinkedHashMap<String, Collection> config = new LinkedHashMap<>();
    private final JsonElement json;
    private final Set<SaveConsumer> pendingSaveConsumers = new HashSet<>();

    ConfigManager(String modID) {
        this.modID = modID;
        jsonPath = Paths.get(FabricLoader.getInstance().getConfigDirectory().toPath().toString(), modID + ".json");
        json = load();
    }

    private JsonElement load() {
        if(!Files.exists(jsonPath)) return JsonNull.INSTANCE;
        try {
            return new Gson().fromJson(new FileReader(jsonPath.toString()), JsonElement.class);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private LinkedHashMap<String, Entry> getContainerEntries(ConfigEntryContainer container) {
        LinkedHashMap<String, Entry> entries = new LinkedHashMap<>();
        Class clazz = container.getClass();
        while (clazz != null) {
            Set<SaveConsumer> saveConsumers = new HashSet<>();
            Iterator<SaveConsumer> iter = pendingSaveConsumers.iterator();
            while (iter.hasNext()) {
                SaveConsumer saveConsumer = iter.next();
                if (saveConsumer.getFieldClass() == clazz) {
                    saveConsumers.add(saveConsumer);
                    pendingSaveConsumers.remove(saveConsumer);
                }
            }
            Arrays.stream(clazz.getDeclaredMethods()).filter(method -> !Modifier.isStatic(method.getModifiers()) && method.isAnnotationPresent(ConfigEntrySaveConsumer.class)).forEach(method -> {
                ConfigEntrySaveConsumer saveConsumerAnnotation = method.getDeclaredAnnotation(ConfigEntrySaveConsumer.class);
                String fieldName = saveConsumerAnnotation.value();
                Class<? extends ConfigEntryContainer> fieldClass = saveConsumerAnnotation.container();
                if (fieldClass == ConfigEntryContainer.class) {
                    saveConsumers.add(new SaveConsumer(method, container, fieldName));
                } else {
                    Map<String, Entry> fieldClassEntries = findEntries(config, fieldClass);
                    if (fieldClassEntries.isEmpty()) {
                        pendingSaveConsumers.add(new SaveConsumer(method, container, fieldName, fieldClass));
                    } else {
                        Entry entry = fieldClassEntries.get(fieldName);
                        if (entry == null) {
                            throw new RuntimeException("Could not find field " + fieldName + " in " + fieldClass + " of save consumer method " + method);
                        }
                        entry.addSaveConsumer(method, container);
                    }
                }
            });
            LinkedHashMap<String, Entry> clazzEntries = new LinkedHashMap<>();
            //TODO: Warnung in der Konsole anzeigen, wenn Container POJO ist (isConfigPOJO() == true) aber trotzdem ein Feld mit @ConfigEntry annotiert ist, oder wenn Container kein POJO ist und Feld mit @ConfigEntry.Ignore annotiert ist
            Arrays.stream(clazz.getDeclaredFields()).filter(field -> !Modifier.isStatic(field.getModifiers()) && (container.isConfigPOJO() && !field.isAnnotationPresent(ConfigEntry.Ignore.class) || field.isAnnotationPresent(ConfigEntry.class))).forEach(field -> {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                String translationKey = null;
                if (field.isAnnotationPresent(ConfigEntry.TranslationKey.class)) {
                    translationKey = field.getDeclaredAnnotation(ConfigEntry.TranslationKey.class).value();
                    if (StringUtils.isBlank(translationKey)) {
                        throw new RuntimeException("Translation key for entry field " + field + " was blank!");
                    }
                }
                Entry entry;
                if (field.isAnnotationPresent(ConfigEntry.Integer.Bound.class)) {
                    if (field.getType() != Integer.TYPE) {
                        throw new RuntimeException("Cannot apply integer bound to non integer field " + field + "!");
                    }
                    ConfigEntry.Integer.Bound bound = field.getDeclaredAnnotation(ConfigEntry.Integer.Bound.class);
                    entry = new BoundedEntry<>(field, Integer.TYPE, container, translationKey, bound.min(), bound.max());
                } else if (field.isAnnotationPresent(ConfigEntry.Long.Bound.class)) {
                    if (field.getType() != Long.TYPE) {
                        throw new RuntimeException("Cannot apply long bound to non long field " + field + "!");
                    }
                    ConfigEntry.Long.Bound bound = field.getDeclaredAnnotation(ConfigEntry.Long.Bound.class);
                    entry = new BoundedEntry<>(field, Long.TYPE, container, translationKey, bound.min(), bound.max());
                } else {
                    entry = new Entry<>(field, field.getType(), container, translationKey);
                }
                String fieldName = field.getName();
                saveConsumers.removeIf(saveConsumer -> {
                    if (!saveConsumer.getFieldName().equals(fieldName)) {
                        return false;
                    }
                    entry.addSaveConsumer(saveConsumer.getMethod(), saveConsumer.getParentObject());
                    return true;
                });
                clazzEntries.put(fieldName, entry);
            });
            if (!saveConsumers.isEmpty()) {
                SaveConsumer saveConsumer = saveConsumers.iterator().next();
                throw new RuntimeException("Could not find field " + saveConsumer.getFieldName() + " of save consumer method " + saveConsumer.getMethod());
            }
            clazzEntries.putAll(entries);
            entries = clazzEntries;
            clazz = clazz.getSuperclass();
        }
        return entries;
    }

    private Map<String, Entry> findEntries(LinkedHashMap<String, Collection> collections, Class<? extends ConfigEntryContainer> parentClass) {
        Map<String, Entry> entries = new HashMap<>();
        for (Collection collection : collections.values()) {
            entries.putAll(collection.getEntries().entrySet().stream().filter(entry -> entry.getValue().getParentObject().getClass() == parentClass).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
            entries.putAll(findEntries(collection.getCollections(), parentClass));
        }
        return entries;
    }

    public void register(ConfigCategory... categories) {
        Arrays.stream(categories).forEach(category -> registerCategory(config, category, true));
    }

    private void registerCategory(LinkedHashMap<String, Collection> configMap, ConfigCategory category, boolean applyJson) {
        String categoryID = category.getConfigCategoryID();
        if (StringUtils.isBlank(categoryID)) {
            throw new RuntimeException("Category ID of " + category.getClass() + " was null or blank!");
        }
        if (configMap.containsKey(categoryID)) {
            throw new RuntimeException("Duplicate category ID found: " + categoryID);
        }
        Collection collection = new me.lortseam.completeconfig.collection.Collection();
        configMap.put(categoryID, collection);
        registerContainer(collection, category);
        if (collection.getEntries().isEmpty() && collection.getCollections().isEmpty()) {
            configMap.remove(categoryID);
            return;
        }
        if (applyJson) {
            new GsonBuilder()
                    .registerTypeAdapter(CollectionsDeserializer.TYPE, new CollectionsDeserializer(configMap, categoryID))
                    .create()
                    .fromJson(json, CollectionsDeserializer.TYPE);
        }
    }

    private void registerContainer(Collection collection, ConfigEntryContainer container) {
        if (!findEntries(config, container.getClass()).isEmpty()) {
            throw new RuntimeException("An instance of " + container.getClass() + " is already registered!");
        }
        collection.getEntries().putAll(getContainerEntries(container));
        ConfigEntryContainer[] containers = container.getConfigEntryContainers();
        if (containers != null) {
            for (ConfigEntryContainer c : containers) {
                if (c instanceof ConfigCategory) {
                    registerCategory(collection.getCollections(), (ConfigCategory) c, false);
                } else {
                    registerContainer(collection, c);
                    collection.getEntries().putAll(getContainerEntries(c));
                }
            }
        }
    }

    private String joinIDs(String... ids) {
        return String.join(".", ids);
    }

    private String buildTranslationKey(String... ids) {
        return joinIDs("config", modID, joinIDs(ids));
    }

    public Screen getConfigScreen(Screen parentScreen) {
        ConfigBuilder builder = ConfigBuilder
                .create()
                .setParentScreen(parentScreen)
                .setTitle(buildTranslationKey("title"))
                .setSavingRunnable(this::save);
        config.forEach((categoryID, category) -> {
            me.shedaniel.clothconfig2.api.ConfigCategory configCategory = builder.getOrCreateCategory(buildTranslationKey(categoryID));
            for (AbstractConfigListEntry entry : buildCollection(categoryID, category)) {
                configCategory.addEntry(entry);
            }
        });
        return builder.build();
    }

    private List<AbstractConfigListEntry> buildCollection(String parentID, Collection collection) {
        List<AbstractConfigListEntry> list = new ArrayList<>();
        collection.getEntries().forEach((entryID, entry) -> {
            ConfigEntryBuilder builder = ConfigEntryBuilder.create();
            FieldBuilder fieldBuilder = null;
            Object value = entry.getValue();
            String translationKey = entry.getTranslationKey() != null ? buildTranslationKey(entry.getTranslationKey()) : buildTranslationKey(parentID, entryID);
            if (value instanceof Enum) {
                Enum enumValue = (Enum) value;
                fieldBuilder = builder.startEnumSelector(translationKey, enumValue.getDeclaringClass(), enumValue)
                        .setDefaultValue((Enum) entry.getDefaultValue())
                        .setEnumNameProvider(e -> I18n.translate(joinIDs(translationKey, CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, ((Enum) e).name()))))
                        .setSaveConsumer(entry::setValue);
            } else if (value instanceof Boolean) {
                fieldBuilder = builder.startBooleanToggle(translationKey, (Boolean) value)
                        .setDefaultValue((Boolean) entry.getDefaultValue())
                        .setSaveConsumer(entry::setValue);
            } else if (value instanceof Integer) {
                if (entry instanceof BoundedEntry) {
                    BoundedEntry<Integer> boundedIntEntry = (BoundedEntry<Integer>) entry;
                    fieldBuilder = builder.startIntSlider(translationKey, (Integer) value, boundedIntEntry.getMin(), boundedIntEntry.getMax())
                            .setDefaultValue(boundedIntEntry.getDefaultValue())
                            .setSaveConsumer(boundedIntEntry::setValue);
                } else {
                    fieldBuilder = builder.startIntField(translationKey, (Integer) value)
                            .setDefaultValue((Integer) entry.getDefaultValue())
                            .setSaveConsumer(entry::setValue);
                }
            } else if (value instanceof Long) {
                if (entry instanceof BoundedEntry) {
                    BoundedEntry<Long> boundedLongEntry = (BoundedEntry<Long>) entry;
                    fieldBuilder = builder.startLongSlider(translationKey, (Long) value, boundedLongEntry.getMin(), boundedLongEntry.getMax())
                            .setDefaultValue(boundedLongEntry.getDefaultValue())
                            .setSaveConsumer(boundedLongEntry::setValue);
                } else {
                    fieldBuilder = builder.startIntField(translationKey, (Integer) value)
                            .setDefaultValue((Integer) entry.getDefaultValue())
                            .setSaveConsumer(entry::setValue);
                }
            } else if (value instanceof Double) {
                fieldBuilder = builder.startDoubleField(translationKey, (Double) value)
                        .setDefaultValue((Double) entry.getDefaultValue())
                        .setSaveConsumer(entry::setValue);
            } else if (value instanceof Float) {
                fieldBuilder = builder.startFloatField(translationKey, (Float) value)
                        .setDefaultValue((Float) entry.getDefaultValue())
                        .setSaveConsumer(entry::setValue);
            }
            if (fieldBuilder == null) {
                throw new RuntimeException("Unable to create config entry field for type " + value.getClass());
            }
            list.add(fieldBuilder.build());
        });
        collection.getCollections().forEach((subcategoryID, c) -> {
            String id = joinIDs(parentID, subcategoryID);
            SubCategoryBuilder subBuilder = ConfigEntryBuilder.create().startSubCategory(buildTranslationKey(id));
            subBuilder.addAll(buildCollection(id, c));
            list.add(subBuilder.build());
        });
        return list;
    }

    private void save() {
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
                    .registerTypeAdapter(EntrySerializer.TYPE, new EntrySerializer())
                    .setPrettyPrinting()
                    .create()
                    .toJson(config, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void refreshCollections(LinkedHashMap<String, Collection> collections) {
        collections.values().forEach(collection -> {
            collection.getEntries().values().forEach(Entry::getValue);
            refreshCollections(collection.getCollections());
        });
    }

    public void refreshAndSave() {
        refreshCollections(config);
        save();
    }

}