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
public interface ConfigContainer {

    /**
     * Specifies whether this class was solely created for config use.
     *
     * @return whether this class is a config class
     */
    default boolean isConfigObject() {
        return false;
    }

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
        // TODO: Add nested static classes here instead of in Node#resolve
        return ImmutableList.copyOf(classes);
    }

    /**
     * Used to register other containers. They will then be registered at the same level as this container.
     *
     * @return an array of other containers
     * @see Transitive
     */
    default ConfigContainer[] getTransitiveContainers() {
        return new ConfigContainer[0];
    }

    /**
     * Applied to declare that a field of type {@link ConfigContainer} is transitive. The container will then be
     * registered at the same level as this container.
     *
     * <p>If {@link #isConfigObject()} returns {@code true}, all fields of type {@link ConfigContainer} will be
     * resolved. Therefore, the use of this annotation is no longer required in that case.
     *
     * @see #getTransitiveContainers()
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Transitive {

    }

    /**
     * Applied to declare that a field should not be resolved as config entry.
     *
     * <p>Only required if {@link #isConfigObject()} returns {@code true}.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Ignore {

    }
    
}