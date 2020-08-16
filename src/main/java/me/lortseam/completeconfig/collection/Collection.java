package me.lortseam.completeconfig.collection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lortseam.completeconfig.api.ConfigCategory;
import me.lortseam.completeconfig.api.ConfigEntryContainer;
import me.lortseam.completeconfig.exception.IllegalAnnotationTargetException;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class Collection {

    @Getter
    private final EntryMap entries;
    @Getter
    private final CollectionMap collections;

    Collection(ConfigEntryContainer container) {
        this(new EntryMap(), new CollectionMap());
        fill(container);
    }

    private void fill(ConfigEntryContainer container) {
        //TODO
        /*if (!findEntries(config, container.getClass()).isEmpty()) {
            throw new UnsupportedOperationException("An instance of " + container.getClass() + " is already registered");
        }*/
        entries.fill(container);
        List<ConfigEntryContainer> containers = new ArrayList<>();
        for (Class<? extends ConfigEntryContainer> clazz : container.getClasses()) {
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
        }
        containers.addAll(Arrays.asList(container.getTransitiveConfigEntryContainers()));
        for (ConfigEntryContainer c : containers) {
            if (c instanceof ConfigCategory) {
                collections.fill((ConfigCategory) c);
            } else {
                fill(c);
            }
        }
    }

}