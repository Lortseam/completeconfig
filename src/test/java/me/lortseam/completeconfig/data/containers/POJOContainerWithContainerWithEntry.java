package me.lortseam.completeconfig.data.containers;

import me.lortseam.completeconfig.api.ConfigContainer;

public class POJOContainerWithContainerWithEntry implements ConfigContainer {

    private final ContainerWithEntry container = new ContainerWithEntry();

    @Override
    public boolean isConfigPOJO() {
        return true;
    }

}
