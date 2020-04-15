package me.lortseam.completeconfig.api;

public interface ConfigEntryContainer {

    default ConfigEntryContainer[] getConfigEntryContainers() {
        return null;
    }

    default boolean isConfigPOJO() {
        return false;
    }
    
}