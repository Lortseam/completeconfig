package me.lortseam.completeconfig.data;

import lombok.extern.log4j.Log4j2;
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

@Log4j2
public class Collection implements FlatDataPart<ConfigMap> {

    protected final TranslationIdentifier translation;
    private final EntryMap entries;
    private final CollectionMap collections;

    Collection(TranslationIdentifier translation) {
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
        List<ConfigContainer> containers = new ArrayList<>();
        for (Class<? extends ConfigContainer> clazz : container.getConfigClasses()) {
            containers.addAll(Arrays.stream(clazz.getDeclaredFields()).filter(field -> {
                if (container.isConfigPOJO()) {
                    return ConfigContainer.class.isAssignableFrom(field.getType());
                }
                if (field.isAnnotationPresent(ConfigContainer.Transitive.class)) {
                    if (!ConfigContainer.class.isAssignableFrom(field.getType())) {
                        throw new IllegalAnnotationTargetException("Transitive entry " + field + " must implement " + ConfigContainer.class.getSimpleName());
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
            if (container.isConfigPOJO()) {
                resolve(Arrays.stream(clazz.getDeclaredClasses()).filter(nestedClass -> {
                    return ConfigContainer.class.isAssignableFrom(nestedClass) && Modifier.isStatic(nestedClass.getModifiers());
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
                }).filter(Objects::nonNull).collect(Collectors.toList()));
            }
        }
        containers.addAll(Arrays.asList(container.getTransitiveContainers()));
        resolve(containers);
    }

    protected void resolve(java.util.Collection<ConfigContainer> containers) {
        for (ConfigContainer c : containers) {
            if (c instanceof ConfigGroup) {
                collections.resolve((ConfigGroup) c);
            } else {
                resolve(c);
            }
        }
    }

    @Override
    public Iterable<ConfigMap> getChildren() {
        return Arrays.asList(entries, collections);
    }

    boolean isEmpty() {
        return entries.isEmpty() && collections.isEmpty();
    }

}