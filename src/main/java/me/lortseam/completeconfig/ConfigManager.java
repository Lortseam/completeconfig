package me.lortseam.completeconfig;

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
import me.lortseam.completeconfig.entry.Entry;
import me.lortseam.completeconfig.entry.GuiRegistry;
import me.lortseam.completeconfig.exception.IllegalAnnotationParameterException;
import me.lortseam.completeconfig.exception.IllegalAnnotationTargetException;
import me.lortseam.completeconfig.exception.IllegalReturnValueException;
import me.lortseam.completeconfig.saveconsumer.SaveConsumer;
import me.lortseam.completeconfig.serialization.CollectionsDeserializer;
import me.lortseam.completeconfig.serialization.EntrySerializer;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigManager {

    @Getter(AccessLevel.PACKAGE)
    private final String modID;
    private final Path jsonPath;
    private final LinkedHashMap<String, Collection> config = new LinkedHashMap<>();
    private final JsonElement json;
    private final Set<SaveConsumer> pendingSaveConsumers = new HashSet<>();
    @Getter
    private final GuiRegistry guiRegistry = new GuiRegistry();

    ConfigManager(String modID) {
        this.modID = modID;
        jsonPath = Paths.get(FabricLoader.getInstance().getConfigDirectory().toPath().toString(), modID + ".json");
        json = load();
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
                            throw new IllegalAnnotationParameterException("Could not find field " + fieldName + " in " + fieldClass + " requested by save consumer method " + method);
                        }
                        entry.addSaveConsumer(method, container);
                    }
                }
            });
            LinkedHashMap<String, Entry> clazzEntries = new LinkedHashMap<>();
            Arrays.stream(clazz.getDeclaredFields()).filter(field -> {
                if (Modifier.isStatic(field.getModifiers())) {
                    return false;
                }
                if (container.isConfigPOJO()) {
                    return !ConfigEntryContainer.class.isAssignableFrom(field.getType()) && !field.isAnnotationPresent(ConfigEntry.Ignore.class);
                }
                return field.isAnnotationPresent(ConfigEntry.class);
            }).forEach(field -> {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                Entry.Builder builder = Entry.Builder.create(field, container);
                if (field.isAnnotationPresent(ConfigEntry.TranslationKey.class)) {
                    String customTranslationKey = field.getDeclaredAnnotation(ConfigEntry.TranslationKey.class).value();
                    if (StringUtils.isBlank(customTranslationKey)) {
                        throw new IllegalAnnotationParameterException("Translation key for entry field " + field + " must not be blank");
                    }
                    builder.setCustomTranslationKey(customTranslationKey);
                }
                if (field.isAnnotationPresent(ConfigEntry.Integer.Bounded.class)) {
                    if (field.getType() != Integer.TYPE) {
                        throw new IllegalAnnotationTargetException("Cannot apply integer bound to non integer field " + field);
                    }
                    ConfigEntry.Integer.Bounded bounds = field.getDeclaredAnnotation(ConfigEntry.Integer.Bounded.class);
                    builder.setBounds(bounds.min(), bounds.max());
                } else if (field.isAnnotationPresent(ConfigEntry.Long.Bounded.class)) {
                    if (field.getType() != Long.TYPE) {
                        throw new IllegalAnnotationTargetException("Cannot apply long bound to non long field " + field);
                    }
                    ConfigEntry.Long.Bounded bounds = field.getDeclaredAnnotation(ConfigEntry.Long.Bounded.class);
                    builder.setBounds(bounds.min(), bounds.max());
                }
                Entry<?> entry = builder.build();
                if (guiRegistry.getProvider(entry) == null) {
                    throw new UnsupportedOperationException("Could not find gui provider for field type " + entry.getType());
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
                throw new IllegalAnnotationParameterException("Could not find field " + saveConsumer.getFieldName() + " in " + clazz + " requested by save consumer method " + saveConsumer.getMethod());
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
            throw new IllegalReturnValueException("Category ID of " + category.getClass() + " must not be null or blank");
        }
        if (configMap.containsKey(categoryID)) {
            throw new IllegalStateException("Duplicate category ID found: " + categoryID);
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
            throw new UnsupportedOperationException("An instance of " + container.getClass() + " is already registered");
        }
        collection.getEntries().putAll(getContainerEntries(container));
        ConfigEntryContainer[] containers = ArrayUtils.addAll(Arrays.stream(container.getClass().getDeclaredFields()).filter(field -> {
            if (Modifier.isStatic(field.getModifiers())) {
                return false;
            }
            if (container.isConfigPOJO()) {
                return ConfigEntryContainer.class.isAssignableFrom(field.getType());
            }
            if (field.isAnnotationPresent(ConfigEntryContainer.Transitive.class)) {
                if (!ConfigEntryContainer.class.isAssignableFrom(field.getType())) {
                    throw new IllegalAnnotationTargetException("Transitive entry " + field + " must implement ConfigEntryContainer");
                }
                return true;
            }
            return false;
        }).map(field -> {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            try {
                return field.get(container);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }).toArray(ConfigEntryContainer[]::new), Objects.requireNonNull(container.getTransitiveConfigEntryContainers()));
        for (ConfigEntryContainer c : containers) {
            if (c instanceof ConfigCategory) {
                registerCategory(collection.getCollections(), (ConfigCategory) c, false);
            } else {
                registerContainer(collection, c);
                collection.getEntries().putAll(getContainerEntries(c));
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
            String translationKey = entry.getCustomTranslationKey() != null ? buildTranslationKey(entry.getCustomTranslationKey()) : buildTranslationKey(parentID, entryID);
            list.add(guiRegistry.getProvider(entry).build(translationKey, entry.getType(), entry.getValue(), entry.getDefaultValue(), entry.getExtras(), entry::setValue));
        });
        collection.getCollections().forEach((subcategoryID, c) -> {
            String id = joinIDs(parentID, subcategoryID);
            SubCategoryBuilder subBuilder = ConfigEntryBuilder.create().startSubCategory(buildTranslationKey(id));
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
                    .registerTypeAdapter(EntrySerializer.TYPE, new EntrySerializer())
                    .setPrettyPrinting()
                    .create()
                    .toJson(config, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}