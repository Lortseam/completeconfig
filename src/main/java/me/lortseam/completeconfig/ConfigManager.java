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
import me.lortseam.completeconfig.api.ConfigEntryListener;
import me.lortseam.completeconfig.collection.Collection;
import me.lortseam.completeconfig.entry.Entry;
import me.lortseam.completeconfig.exception.IllegalModifierException;
import me.lortseam.completeconfig.gui.GuiRegistry;
import me.lortseam.completeconfig.exception.IllegalAnnotationParameterException;
import me.lortseam.completeconfig.exception.IllegalAnnotationTargetException;
import me.lortseam.completeconfig.exception.IllegalReturnValueException;
import me.lortseam.completeconfig.listener.Listener;
import me.lortseam.completeconfig.serialization.CollectionsDeserializer;
import me.lortseam.completeconfig.serialization.EntrySerializer;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ConfigManager {

    @Getter(AccessLevel.PACKAGE)
    private final String modID;
    private final Path jsonPath;
    private final LinkedHashMap<String, Collection> config = new LinkedHashMap<>();
    private final JsonElement json;
    private final Set<Listener> pendingListeners = new HashSet<>();
    @Getter
    private final GuiRegistry guiRegistry = new GuiRegistry();
    private Supplier<ConfigBuilder> guiBuilder = ConfigBuilder::create;

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
            List<Listener> listeners = new ArrayList<>();
            Iterator<Listener> iter = pendingListeners.iterator();
            while (iter.hasNext()) {
                Listener listener = iter.next();
                if (listener.getFieldClass() == clazz) {
                    listeners.add(listener);
                    pendingListeners.remove(listener);
                }
            }
            Arrays.stream(clazz.getDeclaredMethods()).filter(method -> !Modifier.isStatic(method.getModifiers()) && method.isAnnotationPresent(ConfigEntryListener.class)).forEach(method -> {
                ConfigEntryListener listener = method.getDeclaredAnnotation(ConfigEntryListener.class);
                String fieldName = listener.value();
                Class<? extends ConfigEntryContainer> fieldClass = listener.container();
                if (fieldClass == ConfigEntryContainer.class) {
                    listeners.add(new Listener(method, container, fieldName));
                } else {
                    Map<String, Entry> fieldClassEntries = findEntries(config, fieldClass);
                    if (fieldClassEntries.isEmpty()) {
                        pendingListeners.add(new Listener(method, container, fieldName, fieldClass));
                    } else {
                        Entry entry = fieldClassEntries.get(fieldName);
                        if (entry == null) {
                            throw new IllegalAnnotationParameterException("Could not find field " + fieldName + " in " + fieldClass + " requested by listener method " + method);
                        }
                        entry.addListener(method, container);
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
                if (Modifier.isFinal(field.getModifiers())) {
                    throw new IllegalModifierException("Entry field " + field + " must not be final");
                }
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                Entry.Builder builder = Entry.Builder.create(field, container);
                if (field.isAnnotationPresent(ConfigEntry.class)) {
                    ConfigEntry entryAnnotation = field.getDeclaredAnnotation(ConfigEntry.class);
                    String customTranslationKey = entryAnnotation.customTranslationKey();
                    if (!StringUtils.isBlank(customTranslationKey)) {
                        builder.setCustomTranslationKey(customTranslationKey);
                    }
                    builder.setForceUpdate(entryAnnotation.forceUpdate());
                }
                if (field.isAnnotationPresent(ConfigEntry.Integer.Bounded.class)) {
                    if (field.getType() != int.class && field.getType() != Integer.class) {
                        throw new IllegalAnnotationTargetException("Cannot apply Integer bound to non Integer field " + field);
                    }
                    ConfigEntry.Integer.Bounded bounds = field.getDeclaredAnnotation(ConfigEntry.Integer.Bounded.class);
                    builder.setBounds(bounds.min(), bounds.max());
                } else if (field.isAnnotationPresent(ConfigEntry.Long.Bounded.class)) {
                    if (field.getType() != long.class && field.getType() != Long.class) {
                        throw new IllegalAnnotationTargetException("Cannot apply Long bound to non Long field " + field);
                    }
                    ConfigEntry.Long.Bounded bounds = field.getDeclaredAnnotation(ConfigEntry.Long.Bounded.class);
                    builder.setBounds(bounds.min(), bounds.max());
                } else if (field.isAnnotationPresent(ConfigEntry.Float.Bounded.class)) {
                    if (field.getType() != float.class && field.getType() != Float.class) {
                        throw new IllegalAnnotationTargetException("Cannot apply Float bound to non Float field " + field);
                    }
                    ConfigEntry.Float.Bounded bounds = field.getDeclaredAnnotation(ConfigEntry.Float.Bounded.class);
                    builder.setBounds(bounds.min(), bounds.max());
                } else if (field.isAnnotationPresent(ConfigEntry.Double.Bounded.class)) {
                    if (field.getType() != double.class && field.getType() != Double.class) {
                        throw new IllegalAnnotationTargetException("Cannot apply Double bound to non Double field " + field);
                    }
                    ConfigEntry.Double.Bounded bounds = field.getDeclaredAnnotation(ConfigEntry.Double.Bounded.class);
                    builder.setBounds(bounds.min(), bounds.max());
                }
                Entry<?> entry = builder.build();
                if (guiRegistry.getProvider(entry) == null) {
                    throw new UnsupportedOperationException("Could not find gui provider for field type " + entry.getType());
                }
                String fieldName = field.getName();
                listeners.removeIf(listener -> {
                    if (!listener.getFieldName().equals(fieldName)) {
                        return false;
                    }
                    entry.addListener(listener.getMethod(), listener.getParentObject());
                    return true;
                });
                clazzEntries.put(fieldName, entry);
            });
            if (!listeners.isEmpty()) {
                Listener listener = listeners.iterator().next();
                throw new IllegalAnnotationParameterException("Could not find field " + listener.getFieldName() + " in " + clazz + " requested by listener method " + listener.getMethod());
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
        List<ConfigEntryContainer> containers = new ArrayList<>();
        Class clazz = container.getClass();
        while (clazz != null) {
            containers.addAll(Arrays.stream(clazz.getDeclaredFields()).filter(field -> {
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
                    return (ConfigEntryContainer) field.get(container);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList()));
            clazz = clazz.getSuperclass();
        }
        containers.addAll(Arrays.asList(container.getTransitiveConfigEntryContainers()));
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

    public void setCustomGuiBuilder(Supplier<ConfigBuilder> guiBuilder) {
        this.guiBuilder = guiBuilder;
    }

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
            list.add(guiRegistry.getProvider(entry).build(new TranslatableText(translationKey), entry.getField(), entry.getValue(), entry.getDefaultValue(), entry.getExtras(), entry::setValue));
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
                    .registerTypeAdapter(EntrySerializer.TYPE, new EntrySerializer())
                    .setPrettyPrinting()
                    .create()
                    .toJson(config, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}