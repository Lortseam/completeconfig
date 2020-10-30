package me.lortseam.completeconfig.collection;

import lombok.Getter;
import me.lortseam.completeconfig.api.ConfigCategory;
import me.lortseam.completeconfig.api.ConfigEntryContainer;
import me.lortseam.completeconfig.exception.IllegalAnnotationTargetException;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Collection {

    @Getter
    private final String translationKey;
    @Getter
    private final EntryMap entries;
    @Getter
    private final CollectionMap collections;

    Collection(String modTranslationKey, String parentTranslationKey, ConfigCategory category) {
        String categoryID = category.getConfigCategoryID();
        if (parentTranslationKey == null) {
            translationKey = categoryID;
        } else {
            translationKey = parentTranslationKey + "." + categoryID;
        }
        entries = new EntryMap(modTranslationKey);
        collections = new CollectionMap(modTranslationKey);
        fill(category);
    }

    public Text getText() {
        return new TranslatableText(translationKey);
    }

    private void fill(ConfigEntryContainer container) {
        entries.fill(this, container);
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
                collections.fill(translationKey, (ConfigCategory) c);
            } else {
                fill(c);
            }
        }
    }

}