package me.lortseam.completeconfig.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
//TODO: Rename to ConfigEntryListener
public @interface ConfigEntrySaveConsumer {

    String value();

    Class<? extends ConfigEntryContainer> container() default ConfigEntryContainer.class;

    //TODO: Add boolean to update field regardless of this save consumer

}
