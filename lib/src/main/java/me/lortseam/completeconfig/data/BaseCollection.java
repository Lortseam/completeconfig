package me.lortseam.completeconfig.data;

import com.google.common.collect.Iterables;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigGroup;
import me.lortseam.completeconfig.data.structure.DataPart;
import me.lortseam.completeconfig.data.structure.ParentDataPart;
import me.lortseam.completeconfig.data.text.TranslationIdentifier;
import me.lortseam.completeconfig.exception.IllegalAnnotationTargetException;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

abstract class BaseCollection implements ParentDataPart<DataPart> {

    protected final TranslationIdentifier translation;
    private final EntrySet entries;
    private final CollectionSet collections;

    BaseCollection(TranslationIdentifier translation) {
        this.translation = translation;
        entries = new EntrySet(translation);
        collections = new CollectionSet(translation);
    }

    public Text getText() {
        return translation.toText();
    }

    public java.util.Collection<Entry> getEntries() {
        return Collections.unmodifiableCollection(entries);
    }

    public java.util.Collection<Collection> getCollections() {
        return Collections.unmodifiableCollection(collections);
    }

    void resolve(ConfigContainer container) {
        entries.resolve(container);
        for (Class<? extends ConfigContainer> clazz : container.getConfigClasses()) {
            resolve(Arrays.stream(clazz.getDeclaredFields()).filter(field -> {
                if (field.isAnnotationPresent(ConfigContainer.Transitive.class)) {
                    if (!ConfigContainer.class.isAssignableFrom(field.getType())) {
                        throw new IllegalAnnotationTargetException("Transitive field " + field + " must implement " + ConfigContainer.class.getSimpleName());
                    }
                    return true;
                }
                return false;
            }).map(field -> {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                try {
                    return (ConfigContainer) field.get(container);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList()));
            Class<?>[] nestedClasses = clazz.getDeclaredClasses();
            ArrayUtils.reverse(nestedClasses);
            resolve(Arrays.stream(nestedClasses).filter(nestedClass -> {
                if (nestedClass.isAnnotationPresent(ConfigContainer.Transitive.class)) {
                    if (!ConfigContainer.class.isAssignableFrom(nestedClass)) {
                        throw new IllegalAnnotationTargetException("Transitive " + nestedClass + " must implement " + ConfigContainer.class.getSimpleName());
                    }
                    if (!Modifier.isStatic(nestedClass.getModifiers())) {
                        throw new IllegalAnnotationTargetException("Transitive " + nestedClass + " must be static");
                    }
                    return true;
                }
                return false;
            }).map(nestedClass -> {
                try {
                    Constructor<? extends ConfigContainer> constructor = (Constructor<? extends ConfigContainer>) nestedClass.getDeclaredConstructor();
                    if (!constructor.isAccessible()) {
                        constructor.setAccessible(true);
                    }
                    return constructor.newInstance();
                } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    throw new RuntimeException("Failed to instantiate nested class " + nestedClass, e);
                }
            }).collect(Collectors.toList()));
        }
        resolve(Arrays.asList(container.getTransitives()));
    }

    protected void resolve(Iterable<ConfigContainer> containers) {
        for (ConfigContainer c : containers) {
            if (c instanceof ConfigGroup) {
                collections.resolve((ConfigGroup) c);
            } else {
                resolve(c);
            }
        }
    }

    @Override
    public Iterable<DataPart> getChildren() {
        return Iterables.concat(entries, collections);
    }

    boolean isEmpty() {
        return Iterables.size(getChildren()) == 0;
    }

}
