package me.lortseam.completeconfig.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface ConfigEntryContainer {

    default ConfigEntryContainer[] getTransitiveConfigEntryContainers() {
        return new ConfigEntryContainer[0];
    }

    default boolean isConfigPOJO() {
        return false;
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Transitive {

    }
    
}