package me.lortseam.completeconfig.data;

import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntries;
import me.lortseam.completeconfig.api.ConfigEntry;

import java.lang.reflect.Modifier;
import java.util.Arrays;

public class EntrySet extends OrderedSet<Entry> {

    EntrySet(Parent parent) {
        super(parent);
    }

    void resolve(ConfigContainer container) {
        var root = parent.getRoot();
        for (Class<? extends ConfigContainer> clazz : container.getConfigClasses()) {
            Arrays.stream(clazz.getDeclaredFields()).filter(field -> {
                if (clazz != container.getClass() && Modifier.isStatic(field.getModifiers())) {
                    return false;
                }
                if (clazz.isAnnotationPresent(ConfigEntries.class) && clazz.getAnnotation(ConfigEntries.class).includeAll()) {
                    return !ConfigContainer.class.isAssignableFrom(field.getType()) && !field.isAnnotationPresent(ConfigEntries.Exclude.class) && !Modifier.isTransient(field.getModifiers());
                }
                return field.isAnnotationPresent(ConfigEntry.class);
            }).map(field -> {
                if (Modifier.isFinal(field.getModifiers())) {
                    throw new RuntimeException("Entry field " + field + " must not be final");
                }
                return Entry.create(new EntryOrigin(root, parent, field, container));
            }).forEach(this::add);
        }
    }

}
