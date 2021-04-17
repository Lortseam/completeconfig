package me.lortseam.completeconfig.data;

import lombok.extern.log4j.Log4j2;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntries;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.data.text.TranslationIdentifier;
import me.lortseam.completeconfig.exception.IllegalModifierException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Log4j2
public class EntrySet extends DataSet<Entry> {

    private static final Set<Field> staticFields = new HashSet<>();

    EntrySet(TranslationIdentifier translation) {
        super(translation);
    }

    void resolve(ConfigContainer container) {
        for (Class<? extends ConfigContainer> clazz : container.getConfigClasses()) {
            Arrays.stream(clazz.getDeclaredFields()).filter(field -> {
                if (clazz != container.getClass() && Modifier.isStatic(field.getModifiers())) {
                    return false;
                }
                if (clazz.isAnnotationPresent(ConfigEntries.class)) {
                    return !ConfigContainer.class.isAssignableFrom(field.getType()) && !field.isAnnotationPresent(ConfigContainer.Ignore.class) && !Modifier.isTransient(field.getModifiers());
                }
                return field.isAnnotationPresent(ConfigEntry.class);
            }).map(field -> {
                if (Modifier.isFinal(field.getModifiers())) {
                    throw new IllegalModifierException("Entry field " + field + " must not be final");
                }
                if (Modifier.isStatic(field.getModifiers())) {
                    if (staticFields.contains(field)) {
                        throw new UnsupportedOperationException("Static field has already been resolved: " + field);
                    }
                    staticFields.add(field);
                }
                Entry<?> entry = Entry.of(field, container, translation);
                entry.resolve(field);
                return entry;
            }).forEach(this::add);
        }
    }

}
