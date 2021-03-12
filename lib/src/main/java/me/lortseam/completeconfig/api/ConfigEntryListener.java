package me.lortseam.completeconfig.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Applied to declare that a method listens to a config entry's value changes.
 *
 * <p>The annotated method requires a parameter of the same type as the entry's field and a {@code void} return type.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigEntryListener {

    /**
     * Specifies the name of the entry's field.
     *
     * @return the entry's field name
     */
    String value() default "";

    /**
     * Specifies the class in which the entry's field is declared in. Only required if the listener is not declared
     * in the same class.
     *
     * @return the entry's parent class
     */
    Class<? extends ConfigContainer> container() default ConfigContainer.class;

}
