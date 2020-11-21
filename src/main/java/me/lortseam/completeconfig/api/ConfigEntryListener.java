package me.lortseam.completeconfig.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Applied to declare that a method is a listener for a config entry.
 *
 * <p>A listener method gets called every time the config is saved. It requires a parameter of the same type as the
 * entry's field. This parameter contains the updated value which then can be used to modify the field itself.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigEntryListener {

    /**
     * Specifies the name of the field.
     *
     * @return the entry's field name
     */
    String value() default "";

    /**
     * Specifies the class in which the entry's field is declared in. Only required if the listener is not declared
     * in the same class.
     *
     * @return the entry's class
     */
    Class<? extends ConfigEntryContainer> container() default ConfigEntryContainer.class;

}
