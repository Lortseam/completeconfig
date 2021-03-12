package me.lortseam.completeconfig.api;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

/**
 * A container for config entries.
 */
public interface ConfigContainer {

    default List<Class<? extends ConfigContainer>> getConfigClasses() {
        List<Class<? extends ConfigContainer>> classes = new ArrayList<>();
        Class<? extends ConfigContainer> clazz = getClass();
        while (clazz != null) {
            classes.add(clazz);
            if (!ConfigContainer.class.isAssignableFrom(clazz.getSuperclass())) {
                break;
            }
            clazz = (Class<? extends ConfigContainer>) clazz.getSuperclass();
        }
        return Lists.reverse(ImmutableList.copyOf(classes));
    }

    /**
     * Used to register transitive containers, located at the level of this container.
     *
     * @return an array of containers
     * @see Transitive
     */
    default ConfigContainer[] getTransitives() {
        return new ConfigContainer[0];
    }

    /**
     * Applied to declare that a field or a nested class of type {@link ConfigContainer} is transitive. Transitive
     * containers will be registered at the level of this container.
     *
     * @see #getTransitives()
     */
    @Target({ElementType.FIELD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Transitive {

    }

    /**
     * Applied to declare that a field should not be resolved as config entry.
     * This annotation is needed to exclude fields if the {@link ConfigEntries} annotation was applied to the class.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Ignore {

    }
    
}