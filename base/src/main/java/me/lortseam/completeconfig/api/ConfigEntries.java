package me.lortseam.completeconfig.api;

import me.lortseam.completeconfig.text.TranslationBase;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigEntries {

    /**
     * If {@code true}, every field of the annotated class will be resolved as config entry.
     *
     * <p>Use {@link Exclude} to exclude a field.
     */
    boolean includeAll();

    TranslationBase translationBase() default TranslationBase.INSTANCE;

    /**
     * Applied to declare that a field should not be resolved as config entry.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Exclude {

    }

}
