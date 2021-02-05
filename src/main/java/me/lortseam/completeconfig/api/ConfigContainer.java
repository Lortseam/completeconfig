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
     * Specifies whether this class is a POJO. If true, every field inside this class will be considered to be a config
     * entry.
     *
     * @return whether this class is a POJO
     */
    default boolean isConfigPOJO() {
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
     * <p>This annotation is only required inside non-POJO classes. POJO classes will always register every field of
     * type {@link ConfigContainer}.
     *
     * @see #getTransitiveContainers()
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Transitive {

    }

    /**
     * Can be applied to a field inside a POJO {@link ConfigContainer} class to declare that that field should not
     * be considered to be a config entry.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Ignore {

    }
    
}