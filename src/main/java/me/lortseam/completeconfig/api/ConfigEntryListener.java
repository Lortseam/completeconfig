package me.lortseam.completeconfig.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigEntryListener {

    //TODO: Automatically detect field name dependening on method name (e.g. setExampleValue -> field name is exampleValue)
    String value();

    Class<? extends ConfigEntryContainer> container() default ConfigEntryContainer.class;

}
