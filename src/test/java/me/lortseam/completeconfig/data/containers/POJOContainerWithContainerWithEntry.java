package me.lortseam.completeconfig.data.containers;

import me.lortseam.completeconfig.api.ConfigEntryContainer;

public class POJOContainerWithContainerWithEntry implements ConfigEntryContainer {

    private final ContainerWithEntry container = new ContainerWithEntry();

    @Override
    public boolean isConfigPOJO() {
        return true;
    }

}
