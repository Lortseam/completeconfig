package me.lortseam.completeconfig.data;

import com.google.common.base.CaseFormat;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigEntryContainer;
import me.lortseam.completeconfig.api.ConfigEntryListener;
import me.lortseam.completeconfig.data.text.TranslationIdentifier;
import me.lortseam.completeconfig.exception.IllegalAnnotationParameterException;
import me.lortseam.completeconfig.exception.IllegalModifierException;
import me.lortseam.completeconfig.exception.IllegalReturnTypeException;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntryMap extends ConfigMap<Entry> {

    EntryMap(TranslationIdentifier translation) {
        super(translation);
    }

    void resolve(ConfigEntryContainer container) {
        List<Entry> containerEntries = new ArrayList<>();
        for (Class<? extends ConfigEntryContainer> clazz : container.getConfigClasses()) {
            Arrays.stream(clazz.getDeclaredMethods()).filter(method -> !Modifier.isStatic(method.getModifiers()) && method.isAnnotationPresent(ConfigEntryListener.class)).forEach(method -> {
                ConfigEntryListener listener = method.getDeclaredAnnotation(ConfigEntryListener.class);
                String fieldName = listener.value();
                if (fieldName.equals("")) {
                    if (!method.getName().startsWith("set") || method.getName().equals("set")) {
                        throw new IllegalAnnotationParameterException("Could not detect field name for listener method " + method + ", please provide it inside the annotation");
                    }
                    fieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, method.getName().replaceFirst("set", ""));
                }
                Class<? extends ConfigEntryContainer> fieldClass = listener.container();
                if (fieldClass == ConfigEntryContainer.class) {
                    fieldClass = clazz;
                }
                if (method.getParameterCount() != 1) {
                    throw new IllegalArgumentException("Listener method " + method + " has wrong number of parameters");
                }
                EntryBase<?> entry = Entry.of(fieldName, fieldClass);
                if (!method.getParameterTypes()[0].equals(entry.getType())) {
                    throw new IllegalArgumentException("Listener method " + method + " has wrong argument type");
                }
                if (method.getReturnType() != Void.TYPE) {
                    throw new IllegalReturnTypeException("Listener method " + method + " may not return a type other than void");
                }
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                entry.interact(e -> e.addListener(method, container));
            });
            List<Entry> clazzEntries = new ArrayList<>();
            Arrays.stream(clazz.getDeclaredFields()).filter(field -> {
                if (Modifier.isStatic(field.getModifiers())) {
                    return false;
                }
                if (container.isConfigPOJO()) {
                    return !ConfigEntryContainer.class.isAssignableFrom(field.getType()) && !field.isAnnotationPresent(ConfigEntry.Ignore.class);
                }
                return field.isAnnotationPresent(ConfigEntry.class);
            }).forEach(field -> {
                if (Modifier.isFinal(field.getModifiers())) {
                    throw new IllegalModifierException("Entry field " + field + " must not be final");
                }
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                Entry<?> entry = Entry.Draft.of(field).build(container, translation);
                entry.resolve(field);
                clazzEntries.add(entry);
            });
            containerEntries.addAll(0, clazzEntries);
        }
        for (Entry<?> entry : containerEntries) {
            put(entry.getID(), entry);
        }
    }

}
