package me.lortseam.completeconfig.api;

import org.jetbrains.annotations.Nullable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A container for config entries.
 */
public interface ConfigContainer {

    default Iterable<Class<? extends ConfigContainer>> getConfigClasses() {
        List<Class<? extends ConfigContainer>> classes = new ArrayList<>();
        Class<? extends ConfigContainer> clazz = getClass();
        while (clazz != null) {
            classes.add(clazz);
            if (!ConfigContainer.class.isAssignableFrom(clazz.getSuperclass())) {
                break;
            }
            clazz = (Class<? extends ConfigContainer>) clazz.getSuperclass();
        }
        Collections.reverse(classes);
        return classes;
    }

    /**
     * Used to register transitive containers dynamically.
     *
     * @return a collection of containers
     *
     * @see Transitive
     */
    default @Nullable Collection<ConfigContainer> getTransitives() {
        return null;
    }

    /**
     * Called when an entry of this container gets updated.
     */
    default void onContainerEntryUpdate() {}

    /**
     * Applied to declare that a field or a nested class of type {@link ConfigContainer} is transitive.
     *
     * @see #getTransitives()
     */
    @Target({ElementType.FIELD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Transitive {

    }
    
}