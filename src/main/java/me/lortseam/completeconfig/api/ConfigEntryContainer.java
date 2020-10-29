package me.lortseam.completeconfig.api;

import com.google.common.collect.ImmutableList;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

/**
 * A container of config entries.
 */
public interface ConfigEntryContainer {

    default ConfigEntryContainer[] getTransitiveConfigEntryContainers() {
        return new ConfigEntryContainer[0];
    }

    default boolean isConfigPOJO() {
        return false;
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Transitive {

    }

    default List<Class<? extends ConfigEntryContainer>> getClasses() {
        List<Class<? extends ConfigEntryContainer>> classes = new ArrayList<>();
        Class<? extends ConfigEntryContainer> clazz = getClass();
        while (clazz != null) {
            classes.add(clazz);
            if (!ConfigEntryContainer.class.isAssignableFrom(clazz.getSuperclass())) {
                break;
            }
            clazz = (Class<? extends ConfigEntryContainer>) clazz.getSuperclass();
        }
        return ImmutableList.copyOf(classes);
    }
    
}