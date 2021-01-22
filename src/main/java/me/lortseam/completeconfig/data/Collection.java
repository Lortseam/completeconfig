package me.lortseam.completeconfig.data;

import lombok.Getter;
import me.lortseam.completeconfig.api.ConfigEntryContainer;
import me.lortseam.completeconfig.api.ConfigGroup;
import me.lortseam.completeconfig.data.structure.FlatDataPart;
import me.lortseam.completeconfig.data.text.TranslationIdentifier;
import me.lortseam.completeconfig.exception.IllegalAnnotationTargetException;
import net.minecraft.text.Text;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Collection implements FlatDataPart<ConfigMap> {

    private final TranslationIdentifier translation;
    @Getter
    private final EntryMap entries;
    @Getter
    private final CollectionMap collections;

    Collection(TranslationIdentifier parentTranslation, ConfigGroup group) {
        translation = parentTranslation.append(group.getGroupID());
        entries = new EntryMap(translation);
        collections = new CollectionMap(translation);
        resolve(group);
    }

    public Text getText() {
        return translation.toText();
    }

    private void resolve(ConfigEntryContainer container) {
        entries.resolve(container);
        List<ConfigEntryContainer> containers = new ArrayList<>();
        for (Class<? extends ConfigEntryContainer> clazz : container.getConfigClasses()) {
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
        containers.addAll(Arrays.asList(container.getTransitiveContainers()));
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

}