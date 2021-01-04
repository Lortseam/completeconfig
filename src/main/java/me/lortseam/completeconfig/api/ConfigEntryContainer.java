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

    /**
     * Used to register other containers. They will then be located at the same level as this container.
     *
     * @return an array of other containers
     * @see Transitive
     */
    default ConfigEntryContainer[] getTransitiveConfigEntryContainers() {
        return new ConfigEntryContainer[0];
    }

    /**
     * Specifies whether this class is a POJO. If true, every field inside this class will be considered to be a config
     * entry.
     *
     * @return whether this class is a POJO
     */
    default boolean isConfigPOJO() {
        return false;
    }

    /**
     * Applied to declare that a field of type {@link ConfigEntryContainer} is transitive. The container will then be
     * registered at the same level as this container.
     *
     * @see #getTransitiveConfigEntryContainers()
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Transitive {

    }

    default List<Class<? extends ConfigEntryContainer>> getConfigClasses() {
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