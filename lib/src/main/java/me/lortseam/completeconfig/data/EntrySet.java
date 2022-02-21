package me.lortseam.completeconfig.data;

import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntries;
import me.lortseam.completeconfig.api.ConfigEntry;

import java.lang.reflect.Modifier;
import java.util.Arrays;

public class EntrySet extends SortedSet<Entry> {

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
                if (clazz.isAnnotationPresent(ConfigEntries.class)) {
                    return !ConfigContainer.class.isAssignableFrom(field.getType()) && !field.isAnnotationPresent(ConfigEntries.Exclude.class) && !Modifier.isTransient(field.getModifiers());
                }
                return field.isAnnotationPresent(ConfigEntry.class);
            }).map(field -> {
                if (Modifier.isFinal(field.getModifiers())) {
                    throw new AssertionError("Entry field " + field + " must not be final");
                }
                return Entry.of(root, parent, field, container);
            }).forEach(this::add);
        }
    }

}
