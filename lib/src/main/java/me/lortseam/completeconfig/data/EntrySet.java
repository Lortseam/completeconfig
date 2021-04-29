package me.lortseam.completeconfig.data;

import lombok.extern.log4j.Log4j2;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntries;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.data.text.TranslationIdentifier;
import me.lortseam.completeconfig.exception.IllegalModifierException;

import java.lang.reflect.Modifier;
import java.util.Arrays;

@Log4j2
public class EntrySet extends DataSet<Entry> {

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
                    return !ConfigContainer.class.isAssignableFrom(field.getType()) && !field.isAnnotationPresent(ConfigEntries.Exclude.class) && !Modifier.isTransient(field.getModifiers());
                }
                return field.isAnnotationPresent(ConfigEntry.class);
            }).map(field -> {
                if (Modifier.isFinal(field.getModifiers())) {
                    throw new IllegalModifierException("Entry field " + field + " must not be final");
                }
                Entry<?> entry = Entry.of(field, Modifier.isStatic(field.getModifiers()) ? null : container, translation);
                entry.resolve(field);
                return entry;
            }).forEach(this::add);
        }
    }

}
