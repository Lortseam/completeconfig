package me.lortseam.completeconfig.data;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import me.lortseam.completeconfig.api.ConfigEntryContainer;
import me.lortseam.completeconfig.api.ConfigGroup;
import me.lortseam.completeconfig.data.structure.FlatDataPart;
import me.lortseam.completeconfig.data.text.TranslationIdentifier;
import me.lortseam.completeconfig.exception.IllegalAnnotationTargetException;
import net.minecraft.text.Text;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
public class Collection implements FlatDataPart<ConfigMap> {

    protected final TranslationIdentifier translation;
    @Getter
    private final EntryMap entries;
    @Getter
    private final CollectionMap collections;

    Collection(TranslationIdentifier translation) {
        this.translation = translation;
        entries = new EntryMap(translation);
        collections = new CollectionMap(translation);
    }

    public Text getText() {
        return translation.toText();
    }

    void resolve(ConfigEntryContainer container) {
        entries.resolve(container);
        List<ConfigEntryContainer> containers = new ArrayList<>();
        for (Class<? extends ConfigEntryContainer> clazz : container.getConfigClasses()) {
            containers.addAll(Arrays.stream(clazz.getDeclaredFields()).filter(field -> {
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
            if (container.isConfigPOJO()) {
                resolve(Arrays.stream(clazz.getDeclaredClasses()).filter(ConfigEntryContainer.class::isAssignableFrom).map(innerClass -> {
                    try {
                        Constructor<? extends ConfigEntryContainer> constructor = (Constructor<? extends ConfigEntryContainer>) innerClass.getDeclaredConstructor();
                        if (!constructor.isAccessible()) {
                            constructor.setAccessible(true);
                        }
                        return constructor.newInstance();
                    } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                        logger.error("Failed to instantiate inner config entry container class", e);
                        return null;
                    }
                }).filter(Objects::nonNull).collect(Collectors.toList()));
            }
        }
        containers.addAll(Arrays.asList(container.getTransitiveContainers()));
        resolve(containers);
    }

    protected void resolve(java.util.Collection<ConfigEntryContainer> containers) {
        for (ConfigEntryContainer c : containers) {
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