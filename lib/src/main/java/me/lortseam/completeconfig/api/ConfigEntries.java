package me.lortseam.completeconfig.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Applied to declare that every field of a class should be resolved as config entry.
 *
 * <p>Use {@link ConfigContainer.Ignore} to exclude a field.
 *
 * @see ConfigEntry
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigEntries {

}
