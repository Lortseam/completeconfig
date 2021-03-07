package me.lortseam.completeconfig.data;

import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigGroup;
import me.lortseam.completeconfig.data.structure.FlatDataPart;
import me.lortseam.completeconfig.data.text.TranslationIdentifier;
import me.lortseam.completeconfig.exception.IllegalAnnotationTargetException;
import net.minecraft.text.Text;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

abstract class Node implements FlatDataPart<DataMap> {

    protected final TranslationIdentifier translation;
    private final EntryMap entries;
    private final CollectionMap collections;

    Node(TranslationIdentifier translation) {
        this.translation = translation;
        entries = new EntryMap(translation);
        collections = new CollectionMap(translation);
    }

    public Text getText() {
        return translation.toText();
    }

    public java.util.Collection<Entry> getEntries() {
        return Collections.unmodifiableCollection(entries.values());
    }

    public java.util.Collection<Collection> getCollections() {
        return Collections.unmodifiableCollection(collections.values());
    }

    void resolve(ConfigContainer container) {
        entries.resolve(container);
        for (Class<? extends ConfigContainer> clazz : container.getConfigClasses()) {
            resolve(Arrays.stream(clazz.getDeclaredFields()).filter(field -> {
                if (field.isAnnotationPresent(ConfigContainer.Transitive.class)) {
                    if (!ConfigContainer.class.isAssignableFrom(field.getType())) {
                        throw new IllegalAnnotationTargetException("Transitive field " + field + " must implement " + ConfigContainer.class.getSimpleName());
                    }
                    return true;
                }
                return false;
            }).map(field -> {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                try {
                    return (ConfigContainer) field.get(container);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList()));
            resolve(Arrays.stream(clazz.getDeclaredClasses()).filter(nestedClass -> {
                if (nestedClass.isAnnotationPresent(ConfigContainer.Transitive.class)) {
                    if (!ConfigContainer.class.isAssignableFrom(nestedClass)) {
                        throw new IllegalAnnotationTargetException("Transitive class " + nestedClass + " must implement " + ConfigContainer.class.getSimpleName());
                    }
                    if (!Modifier.isStatic(nestedClass.getModifiers())) {
                        throw new IllegalAnnotationTargetException("Transitive class " + nestedClass + " must be static");
                    }
                    return true;
                }
                return false;
            }).map(nestedClass -> {
                try {
                    Constructor<? extends ConfigContainer> constructor = (Constructor<? extends ConfigContainer>) nestedClass.getDeclaredConstructor();
                    if (!constructor.isAccessible()) {
                        constructor.setAccessible(true);
                    }
                    return constructor.newInstance();
                } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    throw new RuntimeException("Failed to instantiate nested class " + nestedClass, e);
                }
            }).collect(Collectors.toList()));
        }
        resolve(Arrays.asList(container.getTransitives()));
    }

    protected void resolve(Iterable<ConfigContainer> containers) {
        for (ConfigContainer c : containers) {
            if (c instanceof ConfigGroup) {
                collections.resolve((ConfigGroup) c);
            } else {
                resolve(c);
            }
        }
    }

    @Override
    public Iterable<DataMap> getChildren() {
        return Arrays.asList(entries, collections);
    }

    boolean isEmpty() {
        return entries.isEmpty() && collections.isEmpty();
    }

}
