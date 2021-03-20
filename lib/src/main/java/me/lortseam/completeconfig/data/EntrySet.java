package me.lortseam.completeconfig.data;

import com.google.common.base.CaseFormat;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntries;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigEntryListener;
import me.lortseam.completeconfig.data.text.TranslationIdentifier;
import me.lortseam.completeconfig.exception.IllegalAnnotationParameterException;
import me.lortseam.completeconfig.exception.IllegalModifierException;
import me.lortseam.completeconfig.exception.IllegalReturnTypeException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.regex.Pattern;

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
                    return !ConfigContainer.class.isAssignableFrom(field.getType()) && !field.isAnnotationPresent(ConfigContainer.Ignore.class) && !Modifier.isTransient(field.getModifiers());
                }
                return field.isAnnotationPresent(ConfigEntry.class);
            }).map(field -> {
                if (Modifier.isFinal(field.getModifiers())) {
                    throw new IllegalModifierException("Entry field " + field + " must not be final");
                }
                Entry<?> entry = Entry.Draft.of(field, container.getClass()).build(Modifier.isStatic(field.getModifiers()) ? null : container, translation);
                entry.resolve(field);
                return entry;
            }).forEach(this::add);
            Arrays.stream(clazz.getDeclaredMethods()).filter(method -> {
                if (clazz != container.getClass() && Modifier.isStatic(method.getModifiers())) {
                    return false;
                }
                return method.isAnnotationPresent(ConfigEntryListener.class);
            }).forEach(method -> {
                Field field = null;
                Class<? extends ConfigContainer> fieldClass = clazz;
                if (method.isAnnotationPresent(ConfigEntryListener.class)) {
                    ConfigEntryListener listener = method.getDeclaredAnnotation(ConfigEntryListener.class);
                    if (listener.container() != ConfigContainer.class) {
                        fieldClass = listener.container();
                    }
                    if (!listener.value().equals("")) {
                        try {
                            field = fieldClass.getDeclaredField(listener.value());
                        } catch (NoSuchFieldException e) {
                            throw new IllegalAnnotationParameterException(e);
                        }
                    }
                }
                if (field == null && fieldClass == clazz && method.getName().startsWith("set")) {
                    try {
                        field = fieldClass.getDeclaredField(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, method.getName().replaceFirst(Pattern.quote("set"), "")));
                    } catch (NoSuchFieldException ignore) {}
                }
                if (field == null) {
                    throw new IllegalAnnotationParameterException("Could not detect field name for listener method " + method);
                }
                if (method.getParameterCount() != 1) {
                    throw new IllegalArgumentException("Listener method " + method + " has wrong number of parameters");
                }
                EntryBase<?> entry = Entry.of(field, container.getClass());
                if (!method.getParameterTypes()[0].equals(entry.getType())) {
                    throw new IllegalArgumentException("Listener method " + method + " has wrong argument type");
                }
                if (method.getReturnType() != Void.TYPE) {
                    throw new IllegalReturnTypeException("Listener method " + method + " may not return a type other than void");
                }
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                entry.interact(e -> e.addListener(method, Modifier.isStatic(method.getModifiers()) ? null : container));
            });
        }
    }

}
